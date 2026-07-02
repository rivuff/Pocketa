package com.pocket.wallet.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@Builder
public class SagaInstance extends BaseEntitty{
        
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SagaStatus sagaStatus = SagaStatus.INITIATED;

    @Column(name = "context", columnDefinition = "TEXT")
    private String context;

    @Column(name = "current_step")
    private String currentStep;
}
