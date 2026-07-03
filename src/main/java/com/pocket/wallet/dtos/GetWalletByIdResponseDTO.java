package com.pocket.wallet.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.pocket.wallet.entities.Wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetWalletByIdResponseDTO {

    private Long walletId;
    private Long userId;
    private BigDecimal balance;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GetWalletByIdResponseDTO from(Wallet wallet) {
        return GetWalletByIdResponseDTO.builder()
                .walletId(wallet.getId())
                .userId(wallet.getUserId())
                .balance(wallet.getBalance())
                .isActive(wallet.isActive())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }
}
