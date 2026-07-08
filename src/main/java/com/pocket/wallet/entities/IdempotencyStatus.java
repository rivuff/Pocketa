package com.pocket.wallet.entities;

public enum IdempotencyStatus {
    PROCESSING,   // key saved, saga not yet complete
    COMPLETED,    // saga completed, response stored
    FAILED        // saga failed
}
