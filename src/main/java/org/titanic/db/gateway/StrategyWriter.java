package org.titanic.db.gateway;

import org.titanic.db.entity.StrategyEntity;

import java.util.Optional;

/**
 * @author Hanno Skowronek
 */
public interface StrategyWriter {

    StrategyEntity saveStrategy(StrategyEntity strategy);

    void deactivateAll();

    Optional<StrategyEntity> activateById(int strategyId);

    void deactivateById(int strategyId);
}
