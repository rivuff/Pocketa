package com.pocket.wallet.entities;

import java.math.BigDecimal;
import java.math.BigInteger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public class Wallet extends BaseEntitty{
    
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    public boolean hasSufficientBalance(BigDecimal amount){
        return balance.compareTo(amount) >=0;
    }

    public void debit(BigDecimal amount){
        if(!hasSufficientBalance(amount)){
            throw new IllegalArgumentException("Insufficient balance");
        }

        balance = balance.subtract(amount);
    }

    public void credit(BigDecimal amount){
        balance = balance.add(amount);
    }
}
