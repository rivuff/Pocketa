package com.pocket.wallet.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pocket.wallet.services.Saga.SagaStepInterface;
import com.pocket.wallet.services.Saga.Step.CreditDestinationWalletStep;
import com.pocket.wallet.services.Saga.Step.DebitSourceWalletStep;
import com.pocket.wallet.services.Saga.Step.UpdateTransactionStatus;
import com.pocket.wallet.services.Saga.Step.SagaStepFactory.SagaStepType;

@Configuration
public class SagaConfiguration {
    
    @Bean
    public Map<String, SagaStepInterface> sagaStepMap(
        DebitSourceWalletStep debitSourceWalletStep,
        CreditDestinationWalletStep creditDestinationWalletStep,
        UpdateTransactionStatus updateTransactionStatus
    ){
        Map<String, SagaStepInterface> sagaStepMap = new HashMap<>();

        sagaStepMap.put(SagaStepType.CREDIT_DESTINATION_WALLET_STEP.toString(), creditDestinationWalletStep);
        sagaStepMap.put(SagaStepType.DEBIT_SOURCE_WALLET_STEP.toString(), debitSourceWalletStep);
        sagaStepMap.put(SagaStepType.UPDATE_TRANSACTION_WALLET_STEP.toString(), updateTransactionStatus);

        return sagaStepMap; 
    }
}
