package com.pocket.wallet.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import com.pocket.wallet.entities.Transaction;
import com.pocket.wallet.repositories.TransactionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction createTransaction(Long fromWalletId, Long toWalletId, BigDecimal amount, String description){

        log.info("creating a transaction");

        Transaction transaction = Transaction.builder()
                                        .fromWalletId(fromWalletId)
                                        .toWalletId(toWalletId)
                                        .amount(amount)
                                        .description(description)
                                        .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("Transaction created with id", savedTransaction.getId());

        return savedTransaction;    

    }

    public Transaction getTransactionById(Long id){
        return transactionRepository.findById(id).orElseThrow(()-> new RuntimeException("No Transaction with this id found"));
    }

    public List<Transaction> getTransactionByFromWalletId(Long walletId){
        return transactionRepository.findByFromWalletId(walletId);

    }

    public List<Transaction> getTransactionByToWalletId(Long walletId){
        return transactionRepository.findByToWalletId(walletId);

    }
    
    public List<Transaction> getTransactionBySagaIntace(Long instanceId){
        return transactionRepository.findBySagaInstanceId(instanceId);
    }

    public List<Transaction> getTransactionByStatus(TransactionStatus status){
        return transactionRepository.findByStatus(status);
    }

    public void updateTransactionWithSagaInstanceId(Long transactionId, Long sagaInstanceId){
        Transaction transaction = getTransactionById(transactionId);

        transaction.setSagaInstanceId(sagaInstanceId);

        transactionRepository.save(transaction);
        log.info("Transaction updated with saga instance id {}", sagaInstanceId);
    }
 
}
