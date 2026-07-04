package com.pocket.wallet.repositories;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pocket.wallet.entities.Wallet;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long>{

    List<Wallet> findByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w where w.id = :id")
    Optional<Wallet> findByIdWithLock(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("""
        UPDATE Wallet w
        SET w.balance = w.balance + :amount
        WHERE w.userId = :userId
    """)
    int creditToWalletWithUserId(@Param("userId") Long userId,
                                @Param("amount") BigDecimal amount);
} 