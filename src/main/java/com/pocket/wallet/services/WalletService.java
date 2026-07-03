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
    public void debit(Long walletId, BigDecimal amount){
        log.info("Debiting  {} from wallet id {}", amount, walletId);

        Wallet wallet = getWalletById(walletId);
        wallet.debit(amount);

        walletRepository.save(wallet);  
        log.info("Debit successfully from wallet id {}", walletId);
    }

    @Transactional
    public void credit(Long walletId, BigDecimal amount){
        log.info("Crediting  {} from wallet id {}", amount, walletId);

        Wallet wallet = getWalletById(walletId);
        wallet.credit(amount);

        walletRepository.save(wallet);  
        log.info("Crediting successfully from wallet id {}", walletId);
    }

    public BigDecimal getWalletBalance(Long walletId){
        log.info("Getting balance for the wallet {}", walletId);
        return getWalletById(walletId).getBalance();
    }
}
