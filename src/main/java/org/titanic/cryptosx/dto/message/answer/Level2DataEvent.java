package org.titanic.cryptosx.dto.message.answer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.Instant;

/**
 * @author Hanno Skowronek
 */
@AllArgsConstructor
@Data
public class Level2DataEvent {
    private int MDUpdateID;

    private int AccountId;
    private Instant ActionDateTime;
    private int ActionType;
    private double LastTradePrice;
    private int NumberOfOrders;
    private double Price;
    private int ProductPairCode;
    private double Quantity;
    private int Side;
}
