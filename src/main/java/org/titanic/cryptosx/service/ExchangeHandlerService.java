package org.titanic.cryptosx.service;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.WebSocket;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.titanic.cryptosx.dto.message.answer.*;
import org.titanic.enums.*;
import org.titanic.cryptosx.dto.CryptosxAccount;
import org.titanic.cryptosx.dto.message.*;
import org.titanic.cryptosx.dto.message.payload.CancelOrder;
import org.titanic.cryptosx.dto.message.payload.GetAccountPositions;
import org.titanic.cryptosx.dto.message.payload.GetOrderStatus;
import org.titanic.cryptosx.dto.message.payload.SendOrder;
import org.titanic.cryptosx.gateway.AccountHandler;
import org.titanic.cryptosx.gateway.ExchangeHandler;
import org.titanic.cryptosx.gateway.OutsiderAlertHandler;
import org.titanic.db.entity.TransactionEntity;
import org.titanic.db.gateway.TransactionReader;
import org.titanic.db.gateway.TransactionWriter;
import org.titanic.report.ReportService;
import org.titanic.scheduling.StrategyScheduler;
import org.titanic.util.JSONHelper;

import javax.annotation.PostConstruct;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Hanno Skowronek
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeHandlerService implements ExchangeHandler {

    private final AccountHandler accountHandler;

    private final TransactionReader transactionReader;
    private final TransactionWriter transactionWriter;

    private final OutsiderAlertHandler outsiderAlertHandler;
    private final ReportService reportService;

//    @PostConstruct
    @DependsOn({"AccountHandler", "TransactionCleanup"})
    private void init() {
        log.info("Initialising the ExchangeHandler and connecting to cryptosx..");
        List<CryptosxAccount> accounts = accountHandler.getAccounts();
        for (CryptosxAccount acc : accounts) {
            acc.setExchangeHandler(this);
            acc.connect();
        }

//        this.strategyScheduler.addStrategy(StrategyEntity.builder().symbol("TEST").volume(10).priceMin(1.88).priceMax(1.90).durationHourMin(0).durationHourMax(1).telegramUsername("tg_test").active(true).build());

    }

    @Override
    public void handleMessage(CryptosxAccount account, WebSocket webSocket, MessageFrame message) {
        log.debug("Receiving: " + message);
        try {
            switch (FunctionName.valueOf(message.getN())) {
                case AuthenticateUser -> {
                    JsonObject jsonObject = JSONHelper.fromJson(message.getO());
                    if (jsonObject.get("Authenticated").getAsBoolean()) {
                        account.setStatus(AccountStatus.IDLE);
                    } else {
                        log.error("There was an error with the login of an account!");
                        log.error("Error message: {}", message);
                    }
                }
                case SubscribeAccountEvents -> {
                    JsonObject jsonObject = JSONHelper.fromJson(message.getO());
                    if (jsonObject.get("Subscribed").getAsBoolean()) {
                        log.info("Account {} successfully subscribed to all account events.", account.getAccountId());
                    } else {
                        log.error("Account {} did not subscribe to all account events.", account.getAccountId());
                    }

                }
                case OrderStateEvent -> {
                    OrderStateEvent orderStateEvent = JSONHelper.toObject(message.getO(), OrderStateEvent.class);

                    Optional<TransactionEntity> transactionOptional = transactionReader.getTransaction(orderStateEvent.getClientOrderId());

                    if (transactionOptional.isEmpty()) {
                        log.info("Could not find corresponding transaction for an OrderStateEvent. This is most likely an order sent via the website: {}", orderStateEvent);
                        break;
                    }
                    TransactionEntity transaction = transactionOptional.get();

                    if (transaction.getStatus() == TransactionStatus.EXECUTED) {
                        break;
                    }
                    transaction.setOrderId(orderStateEvent.getOrderId());
                    switch (orderStateEvent.getOrderState()) {
                        case Working -> transaction.setStatus(TransactionStatus.ACCEPTED);
                        case FullyExecuted -> transaction.setStatus(TransactionStatus.EXECUTED);
                        case Canceled -> {
                            log.info("Transaction {} has been successfully cancelled.", transaction.getId());
                            transaction.setStatus(TransactionStatus.CANCELLED);
                        }
                        case Expired -> {
                            log.warn("Unexpected expired order: {}", orderStateEvent);
                            transaction.setStatus(TransactionStatus.FAILED);
                        }
                        case Rejected -> {
                            log.warn("Unexpected rejected order: {}", orderStateEvent);
                            transaction.setStatus(TransactionStatus.FAILED);
                        }
                    }
                    transactionWriter.saveTransaction(transaction);
                }
                case OrderTradeEvent -> {
                    OrderTradeEvent orderTradeEvent = JSONHelper.toObject(message.getO(), OrderTradeEvent.class);

                    Optional<TransactionEntity> transactionOptional = transactionReader.getByOrderId(orderTradeEvent.getOrderId());

                    if (transactionOptional.isEmpty()) {
                        log.warn("Could not find corresponding transaction for an OrderTradeEvent.");
                        break;
                    }
                    TransactionEntity transaction = transactionOptional.get();

                    if (orderTradeEvent.getRemainingQuantity() <= 0) {
                        transaction.setStatus(TransactionStatus.EXECUTED);
                    } else {
                        log.warn("Unexpected partial fill on transaction {}.", transaction.getId());
                    }

                    transactionWriter.saveTransaction(transaction);
                }
                case SendOrder -> {
                    JsonObject jsonObject = JSONHelper.fromJson(message.getO());
                    if (jsonObject.has("result") && jsonObject.get("result").getAsBoolean()) {
                        log.info("There as an error with a SendOrder: {}", message);
                    }
                    if (jsonObject.has("status") && jsonObject.get("status").getAsString().equals("Accepted")) {
                        long orderId = jsonObject.get("OrderId").getAsLong();
                        log.info("Order with orderId {} has successfully been sent.", orderId);
                    }

                }
                case GetOrderStatus -> log.info("GetOrderStatus: {}", message);
                case CancelOrder -> {
                    CancelOrderAnswer cancelOrderAnswer = JSONHelper.toObject(message.getO(), CancelOrderAnswer.class);

                    //TODO: Somehow determine which transactions were cancelled
                    if (cancelOrderAnswer.isResult()) {
                        log.info("Some transaction was successfully cancelled: {}", cancelOrderAnswer);
                    } else {
                        log.info("Some transaction was NOT successfully cancelled: {}", cancelOrderAnswer);
                    }
                }
                case Level2UpdateEvent -> outsiderAlertHandler.level2Update(JSONHelper.toLevel2Data(message.getO()));
                case AccountPositionEvent -> {
                    AccountPositionEvent accountPositionEvent = JSONHelper.toObject(message.getO(), AccountPositionEvent.class);
                    if (accountPositionEvent.getProductSymbol().equals("USDT") && accountPositionEvent.getAmount() < 3000){
                        account.setUsdtBalance(accountPositionEvent.getAmount());
                        reportService.createLowCapitalAlert(account.getAccountName(), accountPositionEvent.getProductSymbol(), accountPositionEvent.getAmount());
                    } else if (accountPositionEvent.getProductSymbol().equals("MAMI") && accountPositionEvent.getAmount() < 20000){
                        reportService.createLowCapitalAlert(account.getAccountName(), accountPositionEvent.getProductSymbol(), accountPositionEvent.getAmount());
                    }
                }
                case GetAccountPositions -> {
                    Type listType = new TypeToken<ArrayList<GetAccountPositionsAnswer>>() {}.getType();
                    ArrayList<GetAccountPositionsAnswer> update = JSONHelper.toObject(message.getO(),listType);
                    GetAccountPositionsAnswer usdtPosition = update.stream().filter(s->s.getProductSymbol().equals("USDT")).findFirst().orElse(null);
                    if (usdtPosition != null){
                        account.setUsdtBalance(usdtPosition.getAmount());
                    }
                }
                case SubscribeLevel2, SubscribeTrades, CancelOrderRejectEvent -> {}
                case Ping -> {
                    // not sure how sure to I check here.
                    // how to check the connection liveliness
                    // if the ws send ping (line 107), the Account should have a Status as waiting for response here?
                    // Are we keeping the  websocket to be on for the whole time?
                    // the client told me that he wants to have multiple account in the future for different trading pair
                    JsonObject jsonObject = JSONHelper.fromJson(message.getO());
                    String msg = jsonObject.get("msg").getAsString();
                    if (msg.equals("pong")) {
                        //account.setStatus(AccountStatus.IDLE);
                        log.info("Connect is active");
                    }
                }
                case LogOut -> {
                    JsonObject jsonObject = JSONHelper.fromJson(message.getO());
                    if (jsonObject.has("result") && jsonObject.get("result").getAsBoolean()) {
                        log.info("Logging out account {}", account.getAccountName());
                        account.setStatus(AccountStatus.LOGGED_OUT);
                    } else {
                        log.warn("Logout of account {} unsuccessful.", account.getAccountName());
                    }
                }
                default -> log.info("Couldn't identify the function of following message: {}", message);
            }
        } catch (IllegalArgumentException e) {
            log.info("Unknown FunctionName: {}", message);
        }
    }

    private void getL2Snapshot(CryptosxAccount account) {
        account.sendMessage("GetL2SnapShot", "{\"OMSId\": 1, \"InstrumentId\": 4, \"Depth\": 100}");
    }

    private void getInstruments(CryptosxAccount account) {
        account.sendMessage("GetInstruments", "{\"OMSId\": 1}");
    }

    private void getUserAccountsInfos(CryptosxAccount account) {
        account.sendMessage("GetUserAccountInfos", String.format("{\"OMSId\": 1, \"UserId\": %s, \"Username\": \"%s\"}", account.getUserId(), "gamerCN12@protonmail.com"));
    }

    public long buyOrder(CryptosxAccount account, int instrumentId, double size, double limit, long orderId) {
        SendOrder payload = new SendOrder(instrumentId, 1, account.getAccountId(), 1, orderId, 0, true, 0, size, 2, 0, limit);

//        account.setStatus(AccountStatus.BUYING);
        account.sendMessage("SendOrder", payload);
        return orderId;
    }

    public long sellOrder(CryptosxAccount account, int instrumentId, double size, double limit, long orderId) {
        SendOrder payload = new SendOrder(instrumentId, 1, account.getAccountId(), 1, orderId, 0, true, 1, size, 2, 0, limit);

//        account.setStatus(AccountStatus.SELLING);
        account.sendMessage("SendOrder", payload);
        return orderId;
    }

    @Override
    public void executeTransaction(TransactionEntity transaction) {

        //TODO: get instrumentId from symbol
        if (transaction.getSide() == Side.Buy) {
            buyOrder(accountHandler.getCorrespondingDto(transaction.getAccount()).orElseThrow(), Instrument.MAMIUSDT.toInteger(), transaction.getVolume(), transaction.getPrice(), transaction.getId());
        } else if (transaction.getSide() == Side.Sell) {
            sellOrder(accountHandler.getCorrespondingDto(transaction.getAccount()).orElseThrow(), Instrument.MAMIUSDT.toInteger(), transaction.getVolume(), transaction.getPrice(), transaction.getId());
        }

    }

    @Override
    public void checkUpOnTrade(long buyTransactionId, long sellTransactionId) {
        TransactionEntity buyTransaction = transactionReader.getTransaction(buyTransactionId).orElseThrow();
        TransactionEntity sellTransaction = transactionReader.getTransaction(buyTransactionId).orElseThrow();

        if (buyTransaction.getStatus() == TransactionStatus.EXECUTED && sellTransaction.getStatus() != TransactionStatus.EXECUTED) {
            log.warn("Outsider activity! Someone else sold their coin to us: {}", buyTransaction);
            reportService.createOutsiderTradeAlert(buyTransaction.getSymbol(), buyTransaction.getVolume(), buyTransaction.getPrice(), Side.Sell);
        }
        if (sellTransaction.getStatus() == TransactionStatus.EXECUTED && buyTransaction.getStatus() != TransactionStatus.EXECUTED) {
            log.warn("Outsider activity! Someone else bought our coin: {}", sellTransaction);
            reportService.createOutsiderTradeAlert(sellTransaction.getSymbol(), sellTransaction.getVolume(), sellTransaction.getPrice(), Side.Buy);
        }

        if (buyTransaction.getStatus() == TransactionStatus.ACCEPTED || buyTransaction.getStatus() == TransactionStatus.OPEN || buyTransaction.getStatus() == TransactionStatus.SCHEDULED) {
            log.warn("Cancelling transaction {} as it wasn't executed as expected.", buyTransactionId);
            this.cancelTransaction(buyTransaction);
        }
        if (sellTransaction.getStatus() == TransactionStatus.ACCEPTED || sellTransaction.getStatus() == TransactionStatus.OPEN || sellTransaction.getStatus() == TransactionStatus.SCHEDULED) {
            log.warn("Cancelling transaction {} as it wasn't executed as expected.", sellTransactionId);
            this.cancelTransaction(sellTransaction);
        }

    }

    @Override
    public void cancelTransaction(TransactionEntity transaction) {
        CryptosxAccount account = getCryptosxAccountFromTransaction(transaction);
        if (account == null) return;
        CancelOrder cancelOrder = CancelOrder.builder().OMSId(1).AccountId(account.getAccountId()).ClOrderId(transaction.getId()).OrderId(transaction.getOrderId()).build();
        account.sendMessage("CancelOrder", cancelOrder);
    }

    @Override
    public void getOrderStatus(TransactionEntity transaction) {
        CryptosxAccount account = getCryptosxAccountFromTransaction(transaction);
        if (account == null) return;
        GetOrderStatus getOrderStatus = new GetOrderStatus(1, account.getAccountId(), transaction.getOrderId());

        account.sendMessage("GetOrderStatus", getOrderStatus);
    }

    @Nullable
    private CryptosxAccount getCryptosxAccountFromTransaction(TransactionEntity transaction) {
        Optional<CryptosxAccount> accountOptional = accountHandler.getCorrespondingDto(transaction.getAccount());
        if (accountOptional.isEmpty()) {
            log.error("The account for the given transaction could not be found: {}", transaction);
            return null;
        }
        return accountOptional.get();
    }

    @Override
    public void getOrderStatus(CryptosxAccount account, long orderId) {
        account.sendMessage("GetOrderStatus", new GetOrderStatus(1, account.getAccountId(), orderId));
    }


    @Override
    public boolean ping(CryptosxAccount account) {
//        account.setStatus(AccountStatus.Ping);
        account.sendMessage("Ping","");
        return false;
    }

    @Override
    public void getAccountPositions(CryptosxAccount account){
        account.sendMessage("GetAccountPositions", new GetAccountPositions(1, account.getAccountId()));
    }
}
