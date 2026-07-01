package com.pocket.wallet.entities;

public enum StepStatus {
    PENDING, 
    RUNNING,
    COMPLETED,
    FAILED,
    COMPENSATING,
    COMPENSATED,
    SKIPPED
}
