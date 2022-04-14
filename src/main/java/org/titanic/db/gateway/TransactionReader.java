package org.titanic.db.gateway;

import org.titanic.db.entity.TransactionEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * @author Hanno Skowronek
 */
public interface TransactionReader {

    Optional<TransactionEntity> getTransaction(long transactionId);

    List<TransactionEntity> getAllTransactions();

    List<TransactionEntity> getAllScheduledTransactions();

    List<TransactionEntity> getAllOpenTransactions();

    List<TransactionEntity> getAllTransactionsByStrategyExecution(int executionId);

    Optional<TransactionEntity> getOpenTransactionByOrderId(long orderId);

    Optional<TransactionEntity> getByOrderId(long orderId);

    List<TransactionEntity> getAllTransactionsFromTodayBySymbol(String symbol);

    List<TransactionEntity> getAllTransactionsByStrategyExecutionAndTimestampAfter(int executionId, Instant timestamp);

    boolean existsByOrderId(long orderId);

    boolean existsByAcceptedPriceQuantitySymbol(double price, double quantity, String symbol);

    List<TransactionEntity> getAllAcceptedTransactions();
}
