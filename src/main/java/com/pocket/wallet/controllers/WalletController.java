package com.pocket.wallet.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pocket.wallet.dtos.CreateWalletRequestDTO;
import com.pocket.wallet.dtos.CreateWalletResponseDTO;
import com.pocket.wallet.dtos.CreditWalletRequestDTO;
import com.pocket.wallet.dtos.CreditWalletResponseDTO;
import com.pocket.wallet.dtos.DebitWalletRequestDTO;
import com.pocket.wallet.dtos.DebitWalletResponseDTO;
import com.pocket.wallet.dtos.GetWalletBalanceRequestDTO;
import com.pocket.wallet.dtos.GetWalletBalanceResponseDTO;
import com.pocket.wallet.dtos.GetWalletByIdRequestDTO;
import com.pocket.wallet.dtos.GetWalletByIdResponseDTO;
import com.pocket.wallet.entities.Wallet;
import com.pocket.wallet.services.WalletService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
@Slf4j
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/create")
    public ResponseEntity<CreateWalletResponseDTO> createWallet(@RequestBody CreateWalletRequestDTO request) {
        try {
            Wallet newWallet = walletService.createWallet(request.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(CreateWalletResponseDTO.from(newWallet));
        } catch (Exception e) {
            log.error("Error creating wallet", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<GetWalletByIdResponseDTO> getWalletById(@PathVariable Long id) {
        try {
            Wallet wallet = walletService.getWalletById(id);
            return ResponseEntity.ok(GetWalletByIdResponseDTO.from(wallet));
        } catch (RuntimeException e) {
            log.error("Wallet not found with id {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Error fetching wallet with id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{id}/balance")
    public ResponseEntity<GetWalletBalanceResponseDTO> getWalletBalance(@PathVariable Long id) {
        try {
            Wallet wallet = walletService.getWalletById(id);
            return ResponseEntity.ok(GetWalletBalanceResponseDTO.from(wallet));
        } catch (RuntimeException e) {
            log.error("Wallet not found with id {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Error fetching balance for wallet id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{id}/debit")
    public ResponseEntity<DebitWalletResponseDTO> debitWallet(@PathVariable Long id, @RequestBody DebitWalletRequestDTO request) {
        try {
            walletService.debit(id , request.getAmount());
            Wallet wallet = walletService.getWalletById(id);
            return ResponseEntity.ok(DebitWalletResponseDTO.from(wallet, request.getAmount()));
        } catch (IllegalArgumentException e) {
            log.error("Invalid debit request for wallet id {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (RuntimeException e) {
            log.error("Wallet not found with id {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Error debiting wallet id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{id}/credit")
    public ResponseEntity<CreditWalletResponseDTO> creditWallet(@PathVariable Long id, @RequestBody CreditWalletRequestDTO request) {
        try {
            walletService.credit( id, request.getAmount());
            Wallet wallet = walletService.getWalletById(id);
            return ResponseEntity.ok(CreditWalletResponseDTO.from(wallet, request.getAmount()));
        } catch (RuntimeException e) {
            log.error("Wallet not found with id {}", id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("Error crediting wallet id {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
