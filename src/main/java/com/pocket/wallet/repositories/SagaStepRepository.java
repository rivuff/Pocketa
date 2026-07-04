package com.pocket.wallet.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pocket.wallet.entities.SagaStep;
import com.pocket.wallet.entities.StepStatus;

public interface SagaStepRepository extends JpaRepository<SagaStep, Long>{

    List<SagaStep> findBySagaInstanceId(Long sagaInstanceId);

    List<SagaStep> findBySagaInstanceIdAndStatus(Long sagaInstanceId, StepStatus status);

    Optional<SagaStep> findBySagaInstanceIdAndStepNameAndStatus(Long sagaInstanceId, String stepName, StepStatus status);

    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstanceId = :sagaInstanceId AND s.status = com.pocket.wallet.entities.StepStatus.COMPLETED")
    List<SagaStep> findCompletedSagaStepBySagaInstanceId(@Param("sagaInstanceId") Long sagaInstanceId);

    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstanceId = :sagaInstanceId AND s.status IN (com.pocket.wallet.entities.StepStatus.COMPLETED, com.pocket.wallet.entities.StepStatus.COMPENSATED)")
    List<SagaStep> findCompletedOrCompensatedSagaStepBySagaInstanceId(@Param("sagaInstanceId") Long sagaInstanceId);
}
