package org.titanic.enums;

/**
 * @author Hanno Skowronek
 */
public enum FunctionName {
    Level2UpdateEvent("Level2UpdateEvent"),
    TradeDataUpdateEvent("TradeDataUpdateEvent"),
    OrderStateEvent("OrderStateEvent"),
    OrderTradeEvent("OrderTradeEvent "),
    AuthenticateUser("AuthenticateUser"),
    SubscribeAccountEvents("SubscribeAccountEvents"),
    Ping("Ping"),
    SendOrder("SendOrder"),
    GetOrderStatus("GetOrderStatus"),
    CancelOrderRejectEvent("CancelOrderRejectEvent"),
    SubscribeLevel2("SubscribeLevel2"),
    SubscribeTrades("SubscribeTrades"),
    CancelOrder("CancelOrder"),
    GetAccountPositions("GetAccountPositions"),
    AccountPositionEvent("AccountPositionEvent"),
    LogOut("LogOut");

    private final String value;

    FunctionName(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
