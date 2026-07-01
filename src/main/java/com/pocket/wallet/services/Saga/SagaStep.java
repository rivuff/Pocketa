package com.pocket.wallet.services.Saga;

public interface SagaStep {

    boolean execute(SagaContext context);

    boolean compensate(SagaContext context);

    String stepName();
}