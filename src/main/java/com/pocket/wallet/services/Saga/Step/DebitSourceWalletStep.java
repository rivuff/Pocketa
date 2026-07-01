package com.pocket.wallet.services.Saga.Step;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.pocket.wallet.entities.Wallet;
import com.pocket.wallet.repositories.WalletRepository;
import com.pocket.wallet.services.Saga.SagaContext;
import com.pocket.wallet.services.Saga.SagaStep;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@AllArgsConstructor
@Slf4j
public class DebitSourceWalletStep implements SagaStep{

    WalletRepository walletRepository;

    @Override
    public boolean execute(SagaContext context) {
        Long sourceWalletId = context.getLong("sourceWalletId");

        BigDecimal amount = context.getBigDecimal("amount");

        log.info("Debiting source wallet id {} with {}", sourceWalletId, amount);

        Wallet wallet = walletRepository.findByIdWithLock(sourceWalletId)
                            .orElseThrow(()-> new RuntimeException("Wallet not found"));

        context.put("originalSourceBalance", wallet.getBalance());

        wallet.debit(amount);

        walletRepository.save(wallet);

        log.info("Source wallet debited successfully with balance: {}", wallet.getBalance());

        context.put("originalSourceBalanceAfterDebitValue", wallet.getBalance());

        return true;
    }

    @Override
    public boolean complensate(SagaContext context) {
        Long sourceWalletId = context.getLong("sourceWalletId");

        BigDecimal amount = context.getBigDecimal("amount");

        log.info("Compensating source wallet id {} with {}", sourceWalletId, amount);

        Wallet wallet = walletRepository.findByIdWithLock(sourceWalletId)
                            .orElseThrow(()-> new RuntimeException("Wallet not found"));

        context.put("originalSourceBalance", wallet.getBalance());

        wallet.credit(amount);

        walletRepository.save(wallet);

        log.info("Source wallet complensated successfully with balance: {}", wallet.getBalance());

        context.put("originalSourceBalanceAfterCreditComplensation", wallet.getBalance());

        return true;
    }

    @Override
    public String stepName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stepName'");
    }
    
}
