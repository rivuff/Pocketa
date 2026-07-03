package com.pocket.wallet.dtos;

import com.pocket.wallet.entities.OnboardingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnboardingResponse {
    private Long userId;
    private String name;
    private String email;
    private OnboardingStatus onboardingStatus;
}