package org.titanic.db.service.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.titanic.db.entity.TransactionEntity;
import org.titanic.enums.TransactionStatus;
import org.titanic.db.gateway.TransactionReader;
import org.titanic.db.repository.TransactionJpaRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * @author Hanno Skowronek
 */
@Service
@RequiredArgsConstructor
public class TransactionReaderService implements TransactionReader {

    private final TransactionJpaRepository transactionJpaRepository;

    @Override
    public Optional<TransactionEntity> getTransaction(long transactionId) {
        return transactionJpaRepository.findById(transactionId);
    }

    @Override
    public List<TransactionEntity> getAllTransactions() {
        return transactionJpaRepository.findAll();
    }

    @Override
    public List<TransactionEntity> getAllScheduledTransactions() {
        return transactionJpaRepository.findAllByStatusEquals(TransactionStatus.SCHEDULED);
    }

    @Override
    public List<TransactionEntity> getAllOpenTransactions() {
        return transactionJpaRepository.findAllByStatusEquals(TransactionStatus.OPEN);
    }

    @Override
    public List<TransactionEntity> getAllTransactionsByStrategyExecution(int executionId) {
        return transactionJpaRepository.findAllByExecution_Id(executionId);
    }

    @Override
    public List<TransactionEntity> getAllTransactionsByStrategyExecutionAndTimestampAfter(int executionId, Instant timestamp) {
        return transactionJpaRepository.findAllByExecution_IdAndTimestampAfter(executionId, timestamp);
    }

    @Override
    public boolean existsByOrderId(long orderId) {
        return transactionJpaRepository.existsByOrderId(orderId);
    }

    @Override
    public boolean existsByAcceptedPriceQuantitySymbol(double price, double quantity, String symbol) {
        return transactionJpaRepository.existsByStatusAndPriceAndVolumeAndSymbol(TransactionStatus.ACCEPTED, price, quantity, symbol);
    }

    @Override
    public List<TransactionEntity> getAllAcceptedTransactions() {
        return transactionJpaRepository.findAllByStatusEquals(TransactionStatus.ACCEPTED);
    }

    @Override
    public Optional<TransactionEntity> getOpenTransactionByOrderId(long orderId) {
        return transactionJpaRepository.findByOrderIdAndStatusEquals(orderId, TransactionStatus.OPEN);
    }

    @Override
    public Optional<TransactionEntity> getByOrderId(long orderId) {
        return transactionJpaRepository.findByOrderId(orderId);
    }

    @Override
    public List<TransactionEntity> getAllTransactionsFromTodayBySymbol(String symbol) {
        return transactionJpaRepository.findAllByTimestampAfterAndSymbolEquals(Instant.now().truncatedTo(ChronoUnit.DAYS), symbol);
    }

}
