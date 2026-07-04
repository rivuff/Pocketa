package com.pocket.wallet.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    
}
