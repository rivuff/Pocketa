# Pocketa 💸
> A distributed digital wallet backend built for scale — horizontal database sharding, Saga orchestration, and pessimistic locking for financial consistency.

---

## What is Pocketa?

Pocketa is a production-grade backend system for a digital wallet, designed with the same architectural concerns you'd find in real fintech systems — data distribution across nodes, multi-step transaction consistency, and race condition prevention on concurrent balance updates.

Built with Java and Spring Boot, it uses Apache ShardingSphere to horizontally shard wallet and transaction data across multiple MySQL nodes, and implements the Saga orchestration pattern to coordinate distributed fund transfers with full compensation support on failure.

---

## Architecture Overview

```
Client Request
      │
      ▼
TransferSagaService
      │
      ├──► SagaOrchestrator (creates & persists SagaInstance)
      │
      ├──► Step 1: DebitSourceWalletStep      ← PESSIMISTIC_WRITE lock
      │         └── On fail: compensate
      │
      ├──► Step 2: CreditDestinationWalletStep ← PESSIMISTIC_WRITE lock
      │         └── On fail: compensate both steps
      │
      └──► Step 3: UpdateTransactionStatus
                └── completeSaga / failSaga



```

All saga state (instance + individual steps) is persisted to the database, meaning the system can reason about in-flight transactions even after a restart.

---

## Key Design Decisions

### 1. Horizontal Database Sharding (Apache ShardingSphere)

Data is distributed across multiple MySQL shard nodes using domain-aware sharding strategies — different tables shard on different keys based on their access patterns:

| Table | Sharding Key | Reason |
|---|---|---|
| `user` | `id` | Direct user lookup |
| `wallet` | `user_id` | Co-locate wallet with its owner |
| `transaction` | `from_wallet_id` | Group transactions by source wallet |
| `saga_instance` | `id` | Distributed saga state |
| `saga_step` | `id` | Distributed step tracking |

**Sharding algorithm:** Hash-based inline (`Math.abs(Long.hashCode(key)) % 2 + 1`), ensuring even distribution.

**ID generation:** Snowflake ID strategy via ShardingSphere — globally unique, time-ordered, collision-free across nodes without a central sequence.

---

### 2. Saga Orchestration Pattern

Fund transfers are multi-step operations. If any step fails, previously completed steps must be rolled back. Pocketa uses an **orchestration-based Saga** (not choreography) for explicit, sequential control:

```
Steps in TransferMoneySaga:
  1. DebitSourceWallet
  2. CreditDestinationWallet
  3. UpdateTransactionStatus
```

Each step implements `SagaStepInterface` with both `execute()` and `compensate()` methods. The `SagaOrchestrator` iterates steps, and on any failure calls `compensateSaga()` — rolling back all completed steps in reverse.

**Why orchestration over choreography?**
Financial flows need strict sequential guarantees and a single source of truth for saga state. Choreography-based sagas (event-driven) introduce ordering ambiguity that's unacceptable for debit/credit operations.

---

### 3. Pessimistic Locking to Prevent Double-Spend

Every wallet read before a balance update acquires a `PESSIMISTIC_WRITE` lock:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT w FROM Wallet w WHERE w.userId = :id")
Optional<Wallet> findByIdWithLock(@Param("id") Long id);
```

This prevents two concurrent transactions from reading the same balance and both succeeding — a classic double-spend race condition in wallet systems.

Pre-operation balance is also stored in `SagaContext` to enable accurate compensation (restore exact previous balance rather than reversing a potentially stale value).

---

### 4. Atomic User Onboarding

The `OnboardingService` creates both `User` and `Wallet` entities within a single transaction, preventing orphaned wallet records if user creation partially fails.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot |
| ORM | Spring Data JPA / Hibernate |
| Sharding | Apache ShardingSphere |
| Database | MySQL |
| Build | Gradle |

---

## Project Structure

```
src/main/java/com/pocket/wallet/
├── entities/
│   ├── User.java
│   ├── Wallet.java
│   ├── Transaction.java
│   ├── TransactionStatus.java
│   ├── TransactionType.java
│   ├── SagaInstance.java
│   ├── SagaStep.java
│   └── StepStatus.java
│
├── repositories/
│   ├── WalletRepository.java       ← pessimistic lock queries
│   ├── SagaInstanceRepository.java
│   └── SagaStepRepository.java
│
├── services/
│   ├── TransferSagaService.java    ← entry point for transfers
│   ├── WalletService.java
│   ├── TransactionService.java
│   ├── UserService.java
│   ├── OnboardingService.java
│   └── Saga/
│       ├── SagaOrchestrator.java       ← interface
│       ├── SagaOrchestratorImpl.java   ← implementation
│       ├── SagaContext.java
│       ├── SagaStepInterface.java
│       ├── SagaStepFactory.java
│       └── Step/
│           ├── DebitSourceWalletStep.java
│           ├── CreditDestinationWalletStep.java
│           └── UpdateTransactionStatus.java
```

---

## How a Transfer Works

```
1. Client calls initiateTransfer(fromWalletId, toWalletId, amount)
2. A Transaction record is created with PENDING status
3. A SagaInstance is created and persisted to DB
4. transactionId is linked to sagaInstanceId
5. Saga executes steps sequentially:
     a. Acquire PESSIMISTIC_WRITE lock on source wallet
     b. Debit source wallet balance
     c. Acquire PESSIMISTIC_WRITE lock on destination wallet
     d. Credit destination wallet balance
     e. Update transaction status to COMPLETED
6. If any step fails → compensateSaga() reverses completed steps
7. Transaction marked FAILED, balances restored
```

---

## Running Locally

### Prerequisites
- Java 17+
- MySQL (two databases: `shardwallet1`, `shardwallet2`)
- Gradle

### Setup

```bash
# Clone the repo
git clone https://github.com/rivuff/Pocketa.git
cd Pocketa

# Create the two shard databases in MySQL
CREATE DATABASE shardwallet1;
CREATE DATABASE shardwallet2;

# Update credentials in src/main/resources/sharding.yml
# (replace username/password)

# Run the application
./gradlew bootRun
```

---

## What's Coming Next

- [ ] Idempotency keys on payment APIs to handle client retries safely
- [ ] Kafka integration for async transaction event publishing
- [ ] Redis caching for wallet balance reads
- [ ] Rate limiting on transfer endpoints
- [ ] Docker Compose setup for local multi-node MySQL

---

## Why This Project Exists

Most wallet implementations treat transfers as a single DB update. Real financial systems can't do that — distributed data means you can't wrap everything in one transaction. Pocketa explores how to build correct, consistent, and scalable payment flows using patterns (Saga, sharding, pessimistic locking) that production fintech systems actually use.
