package org.titanic.db.gateway;

import org.titanic.db.entity.StrategyExecutionEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author Hanno Skowronek
 */
public interface StrategyExecutionReader {

    List<StrategyExecutionEntity> getExecutionsOfStrategy(int strategyId);

    Optional<StrategyExecutionEntity> getLatestExecutionOfStrategy(int strategyId);
}
