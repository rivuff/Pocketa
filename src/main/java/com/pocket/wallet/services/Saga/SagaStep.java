package com.pocket.wallet.services.Saga;

public interface SagaStep {

    boolean execute(SagaContext context);

    boolean complensate(SagaContext context);

    String stepName();
}