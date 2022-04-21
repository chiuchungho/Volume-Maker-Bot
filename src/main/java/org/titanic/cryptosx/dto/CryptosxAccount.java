package org.titanic.cryptosx.dto;

import com.google.gson.annotations.SerializedName;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.titanic.cryptosx.dto.message.payload.GetAccountPositions;
import org.titanic.cryptosx.service.CryptosxListener;
import org.titanic.cryptosx.gateway.ExchangeHandler;
import org.titanic.cryptosx.dto.message.MessageFrame;
import org.titanic.cryptosx.dto.message.payload.Payload;
import org.titanic.enums.AccountStatus;
import org.titanic.enums.Instrument;
import org.titanic.util.SpringContextUtils;

/**
 * @author Hanno Skowronek
 */

@RequiredArgsConstructor
@Getter
@ToString
@Slf4j
public class CryptosxAccount extends Payload {

    private transient final String CRYPTOSX_URI = "wss://api.cryptosx.io:10443/WSGateway";

    @SerializedName("APIKey")
    private final String apiKey;

    @SerializedName("Signature")
    private final String signature;

    @SerializedName("UserId")
    private final String userId;

    @SerializedName("Nonce")
    private final String nonce;

    @Setter
    private transient ExchangeHandler exchangeHandler;

    private final transient int accountId;
    private final transient String accountName;

    @Setter
    @Getter
    private transient double usdtBalance = 0;

    @Setter
    private transient AccountStatus status = AccountStatus.LOGGED_OUT;

    @Setter
    private transient boolean subscribedToLevel2;

    // socket
    private transient CryptosxListener listener;
    private transient WebSocket webSocket;
    private transient OkHttpClient client;

    public void connect() {
        client = new OkHttpClient();
        Request request = new Request.Builder().url(CRYPTOSX_URI).build();
        listener = new CryptosxListener(this, exchangeHandler);
        webSocket = client.newWebSocket(request, listener);

        String accountStr = this.toJsonString();
        MessageFrame messageFrame = new MessageFrame(0,0,"AuthenticateUser", accountStr);
        String msg = messageFrame.toJsonString();

        webSocket.send(msg);

        this.subscribeAccountEvents();

        if (subscribedToLevel2) {
            this.subscribeLevel2(Instrument.MAMIUSDT.toInteger());
        }

        getAccountPositions();

    }

    public void disconnect() {
        sendMessage("LogOut", "{}");
    }

    public void sendMessage(String functionName, String payload) {
        MessageFrame messageFrame = new MessageFrame(0, 0, functionName, payload);

        log.info("Sending: " + messageFrame.toJsonString());
        boolean active = webSocket.send(messageFrame.toJsonString());

        int retryCounter = 5;
        while (!active && retryCounter > 0) {
            log.warn("The websocket had an issue sending the message. Trying to reconnect and resending the message. Retries left: {}", retryCounter);

            connect();
            active = webSocket.send(messageFrame.toJsonString());

            retryCounter--;
        }
    }

    public void sendMessage(String functionName, Payload payload) {
        sendMessage(functionName, payload.toJsonString());
    }

    public boolean isIdle() {
        return this.status == AccountStatus.IDLE;
    }

    private void subscribeLevel2(int instrumentId) {
        this.sendMessage("SubscribeLevel2", String.format("{\"OMSId\": 1, \"InstrumentId\": %d, \"Depth\": 10}", instrumentId));
    }

    private void subscribeTrades(int instrumentId) {
        this.sendMessage("SubscribeTrades", String.format("{\"OMSId\": 1, \"InstrumentId\": %d, \"IncludeLastCount\": 10}", instrumentId));
    }

    private void subscribeAccountEvents() {
        this.sendMessage("SubscribeAccountEvents", String.format("{\"AccountId\": %d, \"OMSId\": 1}", this.getAccountId()));
    }

    public void getAccountPositions(){
        this.sendMessage("GetAccountPositions", new GetAccountPositions(1, this.getAccountId()));
    }
}
