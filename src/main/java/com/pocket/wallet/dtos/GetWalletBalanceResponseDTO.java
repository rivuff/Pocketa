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
public class GetWalletBalanceResponseDTO {

    private Long walletId;
    private BigDecimal balance;

    public static GetWalletBalanceResponseDTO from(Wallet wallet) {
        return GetWalletBalanceResponseDTO.builder()
                .walletId(wallet.getId())
                .balance(wallet.getBalance())
                .build();
    }
}
