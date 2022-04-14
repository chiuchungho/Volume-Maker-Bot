package org.titanic.cryptosx.service;

import lombok.RequiredArgsConstructor;
import org.titanic.cryptosx.dto.CryptosxAccount;
import org.titanic.cryptosx.dto.message.MessageFrame;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.titanic.cryptosx.gateway.ExchangeHandler;
import org.titanic.util.JSONHelper;

/**
 * @author Hanno Skowronek
 */
@Slf4j
@RequiredArgsConstructor
public class CryptosxListener extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;

    private final CryptosxAccount account;
    private final ExchangeHandler exchangeHandler;

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        log.info("Successfully connected websocket of account {}.", account.getUserId());
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        exchangeHandler.handleMessage(account, webSocket, JSONHelper.toObject(text, MessageFrame.class));
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        log.info("Receiving: " + bytes.hex());
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        webSocket.close(NORMAL_CLOSURE_STATUS, null);
        log.info("Closing: " + code + " " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        t.printStackTrace();
        log.error(t.getMessage());
    }

}
