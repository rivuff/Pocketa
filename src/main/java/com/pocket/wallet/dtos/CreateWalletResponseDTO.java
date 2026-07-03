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
public class CreateWalletResponseDTO {

    private Long walletId;
    private Long userId;
    private BigDecimal balance;
    private boolean isActive;

    public static CreateWalletResponseDTO from(Wallet wallet) {
        return CreateWalletResponseDTO.builder()
                .walletId(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .isActive(wallet.isActive())
                .build();
    }
}
