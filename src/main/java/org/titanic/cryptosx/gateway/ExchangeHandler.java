package org.titanic.cryptosx.gateway;

import okhttp3.WebSocket;
import org.titanic.cryptosx.dto.CryptosxAccount;
import org.titanic.cryptosx.dto.message.MessageFrame;
import org.titanic.db.entity.TransactionEntity;

/**
 * @author Hanno Skowronek
 */
public interface ExchangeHandler {

    void handleMessage(CryptosxAccount account, WebSocket webSocket, MessageFrame message);

    boolean ping(CryptosxAccount account);

    long buyOrder(CryptosxAccount account, int instrumentId, double size, double limit, long orderId);

    long sellOrder(CryptosxAccount account, int instrumentId, double size, double limit, long orderId);

    void executeTransaction(TransactionEntity transaction);

    void checkUpOnTrade(long buyTransactionId, long sellTransactionId);

    void cancelTransaction(TransactionEntity transaction);

    void getOrderStatus(TransactionEntity transaction);

    void getOrderStatus(CryptosxAccount account, long orderId);

    void getAccountPositions(CryptosxAccount account);
}
