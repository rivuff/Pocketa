package com.pocket.wallet.services.Saga.Step;

import com.pocket.wallet.services.Saga.SagaContext;
import com.pocket.wallet.services.Saga.SagaStep;

public class UpdateTransactionStatus implements SagaStep{

    @Override
    public boolean execute(SagaContext context) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }

    @Override
    public boolean complensate(SagaContext context) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'complensate'");
    }

    @Override
    public String stepName() {
        return "UpdateTransactionStatus";
    }
    
}
 