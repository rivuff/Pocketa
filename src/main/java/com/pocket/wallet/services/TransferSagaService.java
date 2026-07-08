package com.pocket.wallet.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.pocket.wallet.entities.IdempotencyRecord;
import com.pocket.wallet.entities.Transaction;
import com.pocket.wallet.services.Saga.SagaContext;
import com.pocket.wallet.services.Saga.SagaOrchestrator;
import com.pocket.wallet.services.Saga.Step.SagaStepFactory;
import com.pocket.wallet.services.Saga.Step.SagaStepFactory.SagaStepType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferSagaService {

    private final TransactionService transactionService;
    private final SagaOrchestrator sagaOrchestrator;
    private final IdempotencyRecordService idempotencyService;


    public long initiateTransfer(Long fromWalletId,
         Long towalletId,
          BigDecimal amount, 
          String description,
          String idempotencyKey
        ){

            log.info("Initiating transfer from wallet {} to wallet {} with ammount {} and description {}", fromWalletId, towalletId, amount, description);

            boolean claimed = idempotencyService.tryClaimKey(idempotencyKey);

            if (!claimed) {
                // key already exists — check its status
                IdempotencyRecord existing = idempotencyService
                    .findExisting(idempotencyKey).orElseThrow();
        
                return switch (existing.getStatus()) {
                    case PROCESSING -> {
                        boolean isStale = existing.getCreatedAt()
                            .isBefore(LocalDateTime.now().minusMinutes(5));
                    
                        if (isStale) {
                            idempotencyService.resetToProcessing(idempotencyKey);
                            yield runSaga(fromWalletId, towalletId, amount, description, idempotencyKey);
                        } else {
                            throw new ResponseStatusException(HttpStatus.CONFLICT,
                                "Transfer already in progress, retry shortly");
                        }
                    }
                    case COMPLETED  -> Long.parseLong(existing.getResponseBody());
                    case FAILED     -> throw new RuntimeException(
                        "Previous attempt failed: " + existing.getResponseBody());
                };
            }

            long sagaInstanceId = runSaga(fromWalletId, towalletId, amount, description, idempotencyKey);

        //   try {
        //     Transaction transaction = transactionService.createTransaction(fromWalletId, towalletId, amount, description);

        //     SagaContext sagaContext = SagaContext.builder()
        //                             .data(
        //                                 Map.ofEntries(
        //                                     Map.entry("transactionId", transaction.getId()),
        //                                     Map.entry("fromWalletId", fromWalletId),
        //                                     Map.entry("toWalletId", towalletId),
        //                                     Map.entry("amount", amount),
        //                                     Map.entry("description", description)
        //                                 )
        //                             ).build();

        //     Long sagaInstanceId = sagaOrchestrator.startSaga(sagaContext);


        //     log.info("saga instance created with id {}", sagaInstanceId);

        //     transactionService.updateTransactionWithSagaInstanceId(transaction.getId(), sagaInstanceId);
            

        //     executeTransferSaga(sagaInstanceId);

        //     idempotencyService.markCompleted(idempotencyKey, String.valueOf(sagaInstanceId), 200);

        //     return sagaInstanceId;
        //   } catch (Exception e) {
        //     idempotencyService.markFailed(idempotencyKey, e.getMessage());
        //     throw e;
        //   }
            
           return sagaInstanceId;
    }

    private long runSaga(Long fromWalletId, Long toWalletId,
            BigDecimal amount, String description, String idempotencyKey) {
        try {
            Transaction transaction = transactionService
                .createTransaction(fromWalletId, toWalletId, amount, description);

            SagaContext sagaContext = SagaContext.builder()
                .data(Map.ofEntries(
                    Map.entry("transactionId", transaction.getId()),
                    Map.entry("fromWalletId", fromWalletId),
                    Map.entry("toWalletId", toWalletId),
                    Map.entry("amount", amount),
                    Map.entry("description", description)
                )).build();

            Long sagaInstanceId = sagaOrchestrator.startSaga(sagaContext);
            log.info("saga instance created with id {}", sagaInstanceId);

            transactionService.updateTransactionWithSagaInstanceId(
                transaction.getId(), sagaInstanceId);

            executeTransferSaga(sagaInstanceId); // ✅ existing void method, untouched

            idempotencyService.markCompleted(
                idempotencyKey, String.valueOf(sagaInstanceId), 200);

            return sagaInstanceId;

        } catch (Exception e) {
            idempotencyService.markFailed(idempotencyKey, e.getMessage());
            throw e;
        }
    }


    public void executeTransferSaga(Long sagaInstanceId){

        log.info("Execute transfer saga with id {}", sagaInstanceId);

        try {
            for(SagaStepType step : SagaStepFactory.TransferMoneySagaSteps){

                boolean success = sagaOrchestrator.executeStep(sagaInstanceId, step.toString());

                if(!success){
                    log.error("Faild to execute saga step {}", step.toString());

                    sagaOrchestrator.failSaga(sagaInstanceId);
                    return;
                }
            }

            sagaOrchestrator.completeSaga(sagaInstanceId);
            log.info("Transfer saga completed with instance id {}", sagaInstanceId);        

        } catch (Exception e) {
            log.error("Faild to execute transfer saga with id {}", sagaInstanceId, e);

            sagaOrchestrator.failSaga(sagaInstanceId);
        }
    }

}
