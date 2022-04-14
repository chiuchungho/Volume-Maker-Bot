package org.titanic.scheduling.task;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.titanic.cryptosx.gateway.ExchangeHandler;
import org.titanic.db.entity.TransactionEntity;
import org.titanic.util.SpringContextUtils;

import java.util.TimerTask;

/**
 * @author Hanno Skowronek
 */
@AllArgsConstructor
@Slf4j
public class TradeCheckTask extends TimerTask {

    private long buyTransactionId;
    private long sellTransactionId;

    @Override
    public void run() {
        log.info("Checking whether trade between transaction {} and transaction {} executed as expected.", buyTransactionId, sellTransactionId);

        ExchangeHandler exchangeHandlerService = SpringContextUtils.getBean(ExchangeHandler.class);
        exchangeHandlerService.checkUpOnTrade(buyTransactionId, sellTransactionId);
    }
}
