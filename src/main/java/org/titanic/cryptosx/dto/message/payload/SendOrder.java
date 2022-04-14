package org.titanic.cryptosx.dto.message.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/** @author Hanno Skowronek */
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
@Data
public class SendOrder extends Payload {

    private final int InstrumentId;
    private final int OMSId;
    private final int AccountId;
    private final int TimeInForce;
    private final long ClientOrderId;
    private final long OrderIdOCO;
    private final boolean UseDisplayQuantity;
    private final int Side;
    private final double quantity;
    private final int OrderType;
    private final int PegPriceType;
    private final double LimitPrice;
}

