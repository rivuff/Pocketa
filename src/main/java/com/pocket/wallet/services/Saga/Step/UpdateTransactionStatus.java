package com.pocket.wallet.services.Saga.Step;

import org.springframework.stereotype.Service;

import com.pocket.wallet.entities.Transaction;
import com.pocket.wallet.entities.TransactionStatus;
import com.pocket.wallet.repositories.TransactionRepository;
import com.pocket.wallet.services.Saga.SagaContext;
import com.pocket.wallet.services.Saga.SagaStepInterface;
import com.pocket.wallet.services.Saga.Step.SagaStepFactory.SagaStepType;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UpdateTransactionStatus implements SagaStepInterface{

    private final TransactionRepository transactionRepository;

    @Override
    public boolean execute(SagaContext context) {
        Long transactionId = context.getLong("transactionId");

        log.info("Updating transaction status for transaction id {}", transactionId);

        Transaction transaction = transactionRepository.findById(transactionId)
                                        .orElseThrow(()-> new RuntimeException("No transaction with this id found"));

        context.put("originalTransactionStatus", transaction.getStatus());

        transaction.setStatus(TransactionStatus.SUCCESSFUL);

        log.info("Transaction status update for id {}", transaction.getId());

        context.put("originalTransactionStatusAfterUpdate", transaction.getStatus());

        log.info("Successfully updated transaction status");

        return true;
    }

    @Override
    public boolean compensate(SagaContext context) {
        Long transactionId = context.getLong("transactionId");

        log.info("Compensating transaction status for transaction id {}", transactionId);

        TransactionStatus originalStatus = TransactionStatus.valueOf(context.getString("originalTransactionStatus"));

        Transaction transaction = transactionRepository.findById(transactionId)
                                        .orElseThrow(()-> new RuntimeException("No transaction with this id found"));

        transaction.setStatus(originalStatus);


        log.info("Transaction status compensated for transaction id {}", transactionId);

        return true;
    }

    @Override
    public String stepName() {
        return SagaStepType.UPDATE_TRANSACTION_WALLET_STEP.toString();
    }
    
}
 