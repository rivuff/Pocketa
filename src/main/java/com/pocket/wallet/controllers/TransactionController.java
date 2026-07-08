package com.pocket.wallet.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pocket.wallet.dtos.TransferRequestDTO;
import com.pocket.wallet.dtos.TransferResponseDto;
import com.pocket.wallet.entities.Transaction;
import com.pocket.wallet.services.TransactionService;
import com.pocket.wallet.services.TransferSagaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
@Slf4j
public class TransactionController {

    private final TransactionService transactionService;
    private final TransferSagaService transferSagaService;

    @PostMapping
    public ResponseEntity<TransferResponseDto> createTransaction(
        @RequestHeader("Idempotency-Key") String idempotencyKey,
        @RequestBody TransferRequestDTO transferRequestDTO
    ){

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Long sagaInstanceId = transferSagaService.initiateTransfer(transferRequestDTO.getFromWalletId(),transferRequestDTO.getToWalletId(),
                                                transferRequestDTO.getAmount(), transferRequestDTO.getDescription(), idempotencyKey);

        return ResponseEntity.status(HttpStatus.CREATED).body(
            TransferResponseDto.builder()
                .sagaInstanceId(sagaInstanceId)
                .build()
        );
    }
    
}
