package com.pocket.wallet.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pocket.wallet.dtos.CreateOnboardingRequest;
import com.pocket.wallet.dtos.OnboardingResponse;
import com.pocket.wallet.entities.User;
import com.pocket.wallet.services.OnboardingService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/onboard")
public class OnboardingController {
    
    private final OnboardingService onboardingService;

    @PostMapping
    public ResponseEntity<OnboardingResponse> onboardUser(@RequestBody CreateOnboardingRequest request) {
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();

        User onboardedUser = onboardingService.onBoardUser(user);

        OnboardingResponse response = new OnboardingResponse(
                onboardedUser.getId(),
                onboardedUser.getName(),
                onboardedUser.getEmail(),
                onboardedUser.getOnboardingStatus()
        );

        return ResponseEntity.ok(response);
    }
}
