package org.titanic.report;

import org.titanic.db.entity.StrategyExecutionEntity;
import org.titanic.db.entity.TransactionEntity;
import org.titanic.enums.Side;

import java.io.File;
import java.util.List;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
public interface ReportService {

    void createAndExecuteReport(StrategyExecutionEntity strategyExecution);

    void createLowCapitalAlert(String userEmail, String symbol, double balance);

    void createOutsiderOrderAlert(String symbol, double volume, double price, Side side, int actionType);

    void createOutsiderTradeAlert(String symbol, double volume, double price, Side side);
}
