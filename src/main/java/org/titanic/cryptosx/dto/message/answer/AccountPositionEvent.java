package org.titanic.cryptosx.dto.message.answer;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Hanno Skowronek
 */
@RequiredArgsConstructor
@Data
public class AccountPositionEvent {
    private final int OMSId;
    private final int AccountId;
    private final String ProductSymbol;
    private final int ProductId;
    private final double Amount;
    private final double Hold;
    private final double PendingDeposits;
    private final double PendingWithdraws;
    private final double TotalDayDeposits;
    private final double TotalDayWithdraws;
    private final double NotionalHoldAmount;
    private final double NotionalRate;
    private final double TotalDayDepositNotional;
    private final double TotalMonthDepositNotional;
    private final double TotalDayWithdrawNotional;
    private final double TotalMonthWithdrawNotional;
}
