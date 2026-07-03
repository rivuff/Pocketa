package com.pocket.wallet.services;

import org.springframework.stereotype.Service;

import com.pocket.wallet.entities.OnboardingStatus;
import com.pocket.wallet.entities.User;
import com.pocket.wallet.entities.Wallet;
import com.pocket.wallet.repositories.UserRepository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class OnboardingService {

    private final UserService userService;
    private final WalletService walletService;

    public User onBoardUser(User user) {
        User newUser = userService.createUser(user);

        try {
            Wallet newWallet = walletService.createWallet(newUser.getId());
            log.info("wallet created");
            newUser.setOnboardingStatus(OnboardingStatus.COMPLETED);
            log.info("User {} onboarded with wallet {}", newUser.getId(), newWallet.getId());
        } catch (Exception e) {
            log.warn("Wallet creation failed for user {}, marking WALLET_PENDING", newUser.getId(), e);
            newUser.setOnboardingStatus(OnboardingStatus.WALLET_PENDING);
        }

        return userService.updateUser(newUser);// saves the status change either way
    }
    
}