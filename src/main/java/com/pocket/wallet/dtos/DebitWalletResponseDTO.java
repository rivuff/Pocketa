package com.pocket.wallet.dtos;

import java.math.BigDecimal;

import com.pocket.wallet.entities.Wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebitWalletResponseDTO {

    private Long walletId;
    private BigDecimal debitedAmount;
    private BigDecimal balance;

    public static DebitWalletResponseDTO from(Wallet wallet, BigDecimal debitedAmount) {
        return DebitWalletResponseDTO.builder()
                .walletId(wallet.getId())
                .debitedAmount(debitedAmount)
                .balance(wallet.getBalance())
                .build();
    }
}
