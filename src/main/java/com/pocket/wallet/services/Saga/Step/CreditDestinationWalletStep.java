package com.pocket.wallet.services.Saga.Step;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.pocket.wallet.entities.Wallet;
import com.pocket.wallet.repositories.WalletRepository;
import com.pocket.wallet.services.Saga.SagaContext;
import com.pocket.wallet.services.Saga.SagaStepInterface;
import com.pocket.wallet.services.Saga.Step.SagaStepFactory.SagaStepType;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class CreditDestinationWalletStep implements SagaStepInterface{
    
    private final WalletRepository walletRepository;

    @Override
    @Transactional
    public boolean execute(SagaContext context){
        // we have to get the destination wallet id from the context

        Long destinationWalletId = context.getLong("toWalletId");
        BigDecimal amount = context.getBigDecimal("amount");

        log.info("CreditDestinationWalletStep: Executing with destinationWalletId: {}, amount: {}", destinationWalletId, amount);

       
        //we have to fetch the destincation wallet from the database with a lock

        Wallet wallet = walletRepository.findByIdWithLock(destinationWalletId)
        .orElseThrow(
            ()-> new RuntimeException("Wallet not found"));

        log.info("wallet fetched successfully with balance: {}", wallet.getBalance());
        context.put("originalToBalance", wallet.getBalance());

        // we have to credit the destination wallet with the ammount

        wallet.credit(amount);

        walletRepository.save(wallet);

        log.info("wallet credited successfully with balance: {}", wallet.getBalance());
        context.put("updatedToBalance", wallet.getBalance());

        //Update the context with the changes

        return true;
    }

    @Override
    public boolean compensate(SagaContext context){
         // we have to get the destination wallet id from the context

         Long destinationWalletId = context.getLong("toWalletId");
         BigDecimal amount = context.getBigDecimal("amount");
 
         log.info("ComplensateDestinationWalletStep: Executing with destinationWalletId: {}, amount: {}", destinationWalletId, amount);
 
        
         //we have to fetch the destincation wallet from the database with a lock
 
         Wallet wallet = walletRepository.findByIdWithLock(destinationWalletId)
         .orElseThrow(
             ()-> new RuntimeException("Wallet not found"));
 
         log.info("wallet fetched successfully with balance: {}", wallet.getBalance());
 
         // we have to credit the destination wallet with the ammount
 
         wallet.debit(amount);
 
         walletRepository.save(wallet);
 
         context.put("updatedToBalanceAfterCompensation", wallet.getBalance());
         log.info("Credit compensation of destination wallet successfully executed: {}", wallet.getBalance());

         //Update the context with the changes
         return true;
    }

    @Override
    public String stepName(){
        return SagaStepType.CREDIT_DESTINATION_WALLET_STEP.toString();
    }
}
