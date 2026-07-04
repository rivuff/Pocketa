package com.pocket.wallet.entities;


import java.math.BigDecimal;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@DynamicUpdate
public class Transaction extends BaseEntity{

    @Column(name = "from_wallet_id")
    private Long fromWalletId;

    @Column(name = "to_wallet_id")
    private Long toWalletId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING;  
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    @Builder.Default
    private TransactionType type = TransactionType.TRANSFER;

    @Column(name = "description")   
    private String description;

    @Column(name = "saga_instance_id")
    private Long sagaInstanceId;
}
