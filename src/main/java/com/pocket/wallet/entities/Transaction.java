package com.pocket.wallet.entities;


import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Builder
@Data
public class Transaction extends BaseEntitty{

    @Column(name = "from_wallet_id", nullable = false)
    private Long fromWallet;

    @Column(name = "to_wallet_id", nullable = false)
    private Long toWallet;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TransactionStatus status = TransactionStatus.PENDING;  
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable =  false)
    private TransactionType type = TransactionType.TRANSFER;

    @Column(name = "description")
    private String description;

    @Column(name = "saga_instance_id", nullable = false)
    private String sagaInstanceId;
}
