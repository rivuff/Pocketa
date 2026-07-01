package com.pocket.wallet.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pocket.wallet.entities.SagaStep;

public interface SagaStepRepository extends JpaRepository<SagaStep, Long>{

    List<SagaStep> findBySagaInstanceId(Long sagaInstanceId);

    @Query("SELECT s FROM SagaStep s where s.sagaInstanceId = :sagaInstanceId AND s.status = 'COMPLETED'")
    List<SagaStep> findCompletedSagaStepBySagaInstanceId(@Param("sagaInstanceId") Long sagaInstanceId);

    @Query("SELECT s FROM SagaStep s where s.sagaInstanceId = :sagaInstanceId AND s.status IN ('COMPLETED', 'COMPENSATED')")
    List<SagaStep> findCompletedOrCompensatedSagaStepBySagaInstanceId(@Param("sagaInstanceId") Long sagaInstanceId);
}
