package org.titanic.scheduling.task;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.titanic.cryptosx.gateway.ExchangeHandler;
import org.titanic.cryptosx.gateway.OutsiderAlertHandler;
import org.titanic.db.entity.TransactionEntity;
import org.titanic.db.gateway.TransactionReader;
import org.titanic.enums.TransactionStatus;
import org.titanic.db.gateway.TransactionWriter;
import org.titanic.util.SpringContextUtils;

import java.util.TimerTask;

/**
 * @author Hanno Skowronek
 */
@AllArgsConstructor
@Slf4j
public class TransactionExecutionTask extends TimerTask {

    private long transactionId;

    @Override
    public void run() {
        TransactionEntity transaction = SpringContextUtils.getBean(TransactionReader.class).getTransaction(transactionId).orElseThrow();

        if (transaction.getStatus() != TransactionStatus.SCHEDULED) {
            return;
        }
        log.info("Transaction {} is being executed!", transaction.getId());

        TransactionWriter transactionWriter = SpringContextUtils.getBean(TransactionWriter.class);
        transaction.setStatus(TransactionStatus.OPEN);
        transactionWriter.saveTransaction(transaction);

        ExchangeHandler exchangeHandlerService = SpringContextUtils.getBean(ExchangeHandler.class);
        exchangeHandlerService.executeTransaction(transaction);
    }
}
