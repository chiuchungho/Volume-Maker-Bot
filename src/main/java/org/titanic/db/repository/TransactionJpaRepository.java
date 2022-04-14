package org.titanic.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.titanic.db.entity.TransactionEntity;
import org.titanic.enums.TransactionStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * @author Hanno Skowronek
 */
@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {

    List<TransactionEntity> findAllByExecution_Id(int executionId);

    Optional<TransactionEntity> findByOrderIdAndStatusEquals(long orderId, TransactionStatus status);

    Optional<TransactionEntity> findByOrderId(long orderId);

    List<TransactionEntity> findAllByTimestampAfterAndSymbolEquals(Instant timestamp, String symbol);

    List<TransactionEntity> findAllByStatusEquals(TransactionStatus status);

    List<TransactionEntity> findAllByExecution_IdAndTimestampAfter(int executionId, Instant timestamp);

    boolean existsByOrderId(long orderId);

    boolean existsByStatusAndPriceAndVolumeAndSymbol(TransactionStatus status, double price, double volume, String symbol);
}
