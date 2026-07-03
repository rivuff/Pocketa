package com.pocket.wallet.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

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


    public long initiateTransfer(Long fromWalletId,
         Long towalletId,
          BigDecimal amount, 
          String description
        ){

            log.info("Initiating transfer from wallet {} to wallet {} with ammount {} and description {}", fromWalletId, towalletId, amount, description);
            
            Transaction transaction = transactionService.createTransaction(fromWalletId, towalletId, amount, description);

            SagaContext sagaContext = SagaContext.builder()
                                    .data(
                                        Map.ofEntries(
                                            Map.entry("transactionId", transaction.getId()),
                                            Map.entry("fromWalletId", fromWalletId),
                                            Map.entry("toWalletId", towalletId),
                                            Map.entry("amount", amount),
                                            Map.entry("description", description)
                                        )
                                    ).build();

            Long sagaInstanceId = sagaOrchestrator.startSaga(sagaContext);


            log.info("saga instance created with id {}", sagaInstanceId);

            transactionService.updateTransactionWithSagaInstanceId(transaction.getId(), sagaInstanceId);
            

            executeTransferSaga(sagaInstanceId);

            return sagaInstanceId;
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
