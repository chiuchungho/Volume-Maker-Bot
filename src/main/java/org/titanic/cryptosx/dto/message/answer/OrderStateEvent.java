package org.titanic.cryptosx.dto.message.answer;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.titanic.enums.OrderState;
import org.titanic.enums.Side;

import java.time.Instant;

/**
 * @author Hanno Skowronek
 */
@AllArgsConstructor
@Data
public class OrderStateEvent {
    private final Side Side;
    private final int OrderId;
    private final double Price;
    private final double Quantity;
    private final int Instrument;
    private final int Account;
    private final String OrderType;
    private final long ClientOrderId;
    private final OrderState OrderState;
    private final Instant ReceiveTime;
    private final double OrigQuantity;
    private final double QuantityExecuted;
    private final double AvgPrice;
    private final String ChangeReason;
}
