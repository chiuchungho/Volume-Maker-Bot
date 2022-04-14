package org.titanic.db.service.writer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.titanic.db.entity.StrategyExecutionEntity;
import org.titanic.db.gateway.StrategyExecutionWriter;
import org.titanic.db.repository.StrategyExecutionJpaRepository;

/**
 * @author Hanno Skowronek
 */
@Service
@RequiredArgsConstructor
public class StrategyExecutionWriterService implements StrategyExecutionWriter {

    private final StrategyExecutionJpaRepository strategyExecutionJpaRepository;

    @Override
    public StrategyExecutionEntity saveStrategyExecution(StrategyExecutionEntity executionEntity) {
        return strategyExecutionJpaRepository.save(executionEntity);
    }
}
