package org.titanic.cryptosx.dto.message.answer;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

/**
 * @author Hanno Skowronek
 */
@AllArgsConstructor
@Data
public class TradeDataUpdateAnswer {
    private final int TradeId;
    private final int ProductPairCode;
    private final double Quantity;
    private final double Price;
    private final long Order1;
    private final long Order2;
    private final Instant Tradetime;
    private final int Direction;
    private final int TakerSide;
    private final boolean BlockTrade;
    private final int orderClientId;

}
