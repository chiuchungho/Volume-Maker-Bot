package org.titanic.db.gateway;

import org.titanic.db.entity.StrategyExecutionEntity;

/**
 * @author Hanno Skowronek
 */
public interface StrategyExecutionWriter {

    StrategyExecutionEntity saveStrategyExecution(StrategyExecutionEntity executionEntity);
}
