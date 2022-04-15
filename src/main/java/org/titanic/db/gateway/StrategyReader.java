package org.titanic.db.gateway;

import org.titanic.db.entity.StrategyEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author Hanno Skowronek
 */
public interface StrategyReader {

    List<StrategyEntity> getAllStrategies();

    List<StrategyEntity> getAllActiveStrategies();

    Optional<StrategyEntity> getById(int strategyId);

    public List<StrategyEntity> getAllActiveStrategiesBySymbol(String symbol);
}
