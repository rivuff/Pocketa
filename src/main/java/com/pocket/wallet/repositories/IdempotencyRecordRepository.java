package com.pocket.wallet.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pocket.wallet.entities.IdempotencyRecord;

@Repository
public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, Long>{
    Optional<IdempotencyRecord> findByIdempotencyKey(String key);
}
