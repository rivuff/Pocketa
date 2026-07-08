package com.pocket.wallet.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.pocket.wallet.entities.IdempotencyRecord;
import com.pocket.wallet.entities.IdempotencyStatus;
import com.pocket.wallet.repositories.IdempotencyRecordRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IdempotencyRecordService {
    private final IdempotencyRecordRepository repository;

    public boolean tryClaimKey(String key) {
        try {
            IdempotencyRecord record = IdempotencyRecord.builder()
                .idempotencyKey(key)
                .status(IdempotencyStatus.PROCESSING)
                .build();

            repository.save(record);
            return true; // claimed successfully

        } catch (DataIntegrityViolationException e) {
            return false; // another request already claimed it
        }
    }

    // Step 2 — after saga completes, update the record
    public void markCompleted(String key, String responseBody, int statusCode) {
        repository.findByIdempotencyKey(key).ifPresent(record -> {
            record.setStatus(IdempotencyStatus.COMPLETED);
            record.setResponseBody(responseBody);
            record.setStatusCode(statusCode);
            repository.save(record);
        });
    }

    // Step 3 — if saga fails, mark it
    public void markFailed(String key, String errorBody) {
        repository.findByIdempotencyKey(key).ifPresent(record -> {
            record.setStatus(IdempotencyStatus.FAILED);
            record.setResponseBody(errorBody);
            record.setStatusCode(500);
            repository.save(record);
        });
    }

    @Transactional
    public void resetToProcessing(String key) {
        repository.findByIdempotencyKey(key).ifPresent(record -> {
            record.setStatus(IdempotencyStatus.PROCESSING);
            record.setCreatedAt(LocalDateTime.now());
            record.setUpdatedAt(LocalDateTime.now().plusHours(24));
            record.setResponseBody(null);
            record.setStatusCode(0);
            repository.save(record);
        });
    }

    public Optional<IdempotencyRecord> findExisting(String key) {
        return repository.findByIdempotencyKey(key);
    }
}
