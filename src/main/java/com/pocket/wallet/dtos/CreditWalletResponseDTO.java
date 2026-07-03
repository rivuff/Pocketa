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
public class CreditWalletResponseDTO {

    private Long walletId;
    private BigDecimal creditedAmount;
    private BigDecimal balance;

    public static CreditWalletResponseDTO from(Wallet wallet, BigDecimal creditedAmount) {
        return CreditWalletResponseDTO.builder()
                .walletId(wallet.getId())
                .creditedAmount(creditedAmount)
                .balance(wallet.getBalance())
                .build();
    }
}
