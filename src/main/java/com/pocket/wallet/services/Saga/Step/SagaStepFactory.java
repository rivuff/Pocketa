package com.pocket.wallet.services.Saga.Step;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.pocket.wallet.services.Saga.SagaStepInterface;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SagaStepFactory {

    private final Map<String, SagaStepInterface> sagaStepMap;

    public static enum SagaStepType{
        CREDIT_DESTINATION_WALLET_STEP,
        DEBIT_SOURCE_WALLET_STEP,
        UPDATE_TRANSACTION_WALLET_STEP
    }

    public SagaStepInterface getSagaStep(String stepName){
       return sagaStepMap.get(stepName);
    } 
}
