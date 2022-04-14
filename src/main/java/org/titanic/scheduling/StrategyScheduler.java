package org.titanic.scheduling;

import org.titanic.db.entity.StrategyEntity;
import org.titanic.db.entity.TransactionEntity;

/**
 * @author Hanno Skowronek
 */
public interface StrategyScheduler {

    StrategyEntity addStrategy(StrategyEntity strategy);

    void deactivateStrategy(int strategyId);

    void deactivateAllStrategies();

    void activateStrategy(int strategyId);
}
