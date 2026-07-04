package com.pocket.wallet.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Service;

import com.pocket.wallet.entities.Wallet;
import com.pocket.wallet.repositories.WalletRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletService {
    
    private final WalletRepository walletRepository;

    public Wallet createWallet(Long userId){
        log.info("Creating wallet for user {}", userId);

        Wallet wallet = Wallet.builder()
                        .userId(userId)
                        .isActive(true)
                        .balance(BigDecimal.ZERO)
                        .build();

        walletRepository.save(wallet);
        
        log.info("Wallet created with id {}", wallet.getId());

        return wallet;
    }

    public Wallet getWalletById(Long id){
        return walletRepository.findById(id).orElseThrow(()-> new RuntimeException("No wallet with this id found"));
    }

    public List<Wallet> getWalletsByUserId(Long userId){
        return walletRepository.findByUserId(userId);
    }

    @Transactional
    public void debit(Long userId, BigDecimal amount){
        log.info("Debiting  {} from user id {}", amount, userId);

        Wallet wallet = getWalletsByUserId(userId).get(0);

        if(wallet.getBalance().compareTo(amount) < 0){
            throw new RuntimeException("insufficient balance to debit amount from user");
        }
       
        walletRepository.debitFromWalletWithUserId(userId, amount);

        log.info("Debit successfully from user id {}", userId);
    }

    @Transactional
    public void credit(Long userId, BigDecimal amount){
        log.info("Crediting  {} from wallet id {}", amount, userId);

        //Wallet wallet = getWalletsByUserId(userId).get(0);
        walletRepository.creditToWalletWithUserId(userId, amount);

        //walletRepository.save(wallet);  
        log.info("Crediting successfully from wallet with user id {}", userId);
    }

    public BigDecimal getWalletBalance(Long walletId){
        log.info("Getting balance for the wallet {}", walletId);
        return getWalletById(walletId).getBalance();
    }
}
