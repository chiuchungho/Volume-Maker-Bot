package org.titanic.cryptosx.dto.message.answer;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.titanic.enums.Side;

import java.time.Instant;

/**
 * @author Hanno Skowronek
 */
@RequiredArgsConstructor
@Data
public class GetOrderStatusAnswer {

    private final Side Side;
    private final long OrderId;
    private final double Price;
    private final double Quantity;
    private final double DisplayQuantity;
    private final int Instrument;
    private final int Account;
    private final String AccountName;
    private final String OrderType;
    private final long ClientOrderId;
    private final String OrderState;
    private final Instant ReceiveTime;
    private final long ReceiveTimeTicks;
    private final long LastUpdatedTime;
    private final long LastUpdatedTimeTicks;
    private final double OrigQuantity;
    private final double QuantityExecuted;
    private final double GrossValueExecuted;
    private final double ExecutableValue;
    private final double AvgPrice;
    private final int CounterPartyId;
    private final String ChangeReason;
    private final long OrigOrderId;
    private final long OrigClOrdId;
    private final int EnteredBy;
    private final String UserName;
    private final boolean IsQuote;
    private final double InsideAsk;
    private final double InsideAskSize;
    private final double InsideBid;
    private final double InsideBidSize;
    private final double LastTradePrice;
    private final String RejectReason;
    private final boolean IsLockedIn;
    private final String CancelReason;
    private final String OrderFlag;
    private final boolean UseMargin;
    private final double StopPrice;
    private final String PegPriceType;
    private final double PegOffset;
    private final double PegLimitOffset;
    private final String IpAddress;
    private final String ClientOrderIdUuid;
    private final int OMSId;
}
