package org.titanic.scheduling;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.titanic.cryptosx.gateway.ExchangeHandler;
import org.titanic.db.entity.TransactionEntity;
import org.titanic.enums.TransactionStatus;
import org.titanic.db.gateway.TransactionReader;
import org.titanic.db.gateway.TransactionWriter;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author Hanno Skowronek
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionCleanup {

    private final TransactionReader transactionReader;
    private final TransactionWriter transactionWriter;
    private final ExchangeHandler exchangeHandler;

    @PostConstruct
    private void init() {
        List<TransactionEntity> scheduledTransactions = transactionReader.getAllScheduledTransactions();
        log.info("Following scheduled transactions were found after system startup and will be cancelled: {}", scheduledTransactions.stream().map(TransactionEntity::getId).toList());
        for (TransactionEntity transaction : scheduledTransactions) {
            transaction.setStatus(TransactionStatus.CANCELLED);
            transactionWriter.saveTransaction(transaction);
        }

        List<TransactionEntity> openTransactions = transactionReader.getAllOpenTransactions();
        log.info("Following open transactions were found after system startup and will be cancelled: {}", openTransactions.stream().map(TransactionEntity::getId).toList());
        for (TransactionEntity transaction : openTransactions) {
            exchangeHandler.cancelTransaction(transaction);

            transaction.setStatus(TransactionStatus.CANCELLED);
            transactionWriter.saveTransaction(transaction);
        }

        List<TransactionEntity> acceptedTransactions = transactionReader.getAllAcceptedTransactions();
        log.info("Following accepted transactions were found after system startup and will be cancelled: {}", acceptedTransactions.stream().map(TransactionEntity::getId).toList());
        for (TransactionEntity transaction : acceptedTransactions) {
            exchangeHandler.cancelTransaction(transaction);

            transaction.setStatus(TransactionStatus.CANCELLED);
            transactionWriter.saveTransaction(transaction);
        }
    }
}
