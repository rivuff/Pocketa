package com.pocket.wallet.services.Saga.Step;

import java.util.List;
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

    public static final List<SagaStepFactory.SagaStepType> TransferMoneySagaSteps = List.of(
        SagaStepFactory.SagaStepType.DEBIT_SOURCE_WALLET_STEP,
        SagaStepFactory.SagaStepType.CREDIT_DESTINATION_WALLET_STEP,
        SagaStepFactory.SagaStepType.UPDATE_TRANSACTION_WALLET_STEP
    );

    public SagaStepInterface getSagaStep(String stepName){
       return sagaStepMap.get(stepName);
    } 
}
