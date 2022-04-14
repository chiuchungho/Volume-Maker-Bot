package org.titanic.cryptosx.dto.message.answer;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.titanic.enums.Side;

import java.time.Instant;

/**
 * @author Hanno Skowronek
 */
@AllArgsConstructor
@Data
public class OrderTradeEvent {
    private final int OMSId;
    private final long ExecutionId;
    private final long TradeId;
    private final long OrderId;
    private final int AccountId;
    private final String AccountName;
    private final int SubAccountId;
    private final long ClientOrderId;
    private final int InstrumentId;
    private final Side Side;
    private final String OrderType;
    private final double Quantity;
    private final double RemainingQuantity;
    private final double Price;
    private final double Value;
    private final String CounterParty;
    private final int OrderTradeRevision;
    private final String Direction;
    private final boolean IsBlockTrade;
    private final double Fee;
    private final int FeeProductId;
    private final int OrderOriginator;
    private final String UserName;
    private final long TradeTimeMS;
    private final String MakerTaker;
    private final int AdapterTradeId;
    private final double InsideBid;
    private final double InsideBidSize;
    private final double InsideAsk;
    private final double InsideAskSize;
    private final boolean IsQuote;
    private final int CounterPartyClientUserId;
    private final int NotionalProductId;
    private final double NotionalRate;
    private final double NotionalValue;
    private final double NotionalHoldAmount;
    private final Instant TradeTime;
}
