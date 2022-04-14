package org.titanic.cryptosx.dto.message.answer;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Hanno Skowronek
 */
@RequiredArgsConstructor
@Data
public class GetAccountPositionsAnswer {
    private final int OMSId;
    private final int AccountId;
    private final String ProductSymbol;
    private final int ProductId;
    private final double Amount;
    private final double Hold;
    private final int PendingDeposits;
    private final int PendingWithdraws;
    private final int TotalDayDeposits;
    private final int TotalMonthDeposits;
    private final int TotalYearDeposits;
    private final int TotalDayDepositNotional;
    private final int TotalMonthDepositNotional;
    private final int TotalYearDepositNotional;
    private final int TotalDayWithdraws;
    private final int TotalMonthWithdraws;
    private final int TotalYearWithdraws;
    private final int TotalDayWithdrawNotional;
    private final int TotalMonthWithdrawNotional;
    private final int TotalYearWithdrawNotional;
    private final int NotionalProductId;
    private final String NotionalProductSymbol;
    private final double NotionalValue;
    private final double NotionalHoldAmount;
    private final double NotionalRate;
}

