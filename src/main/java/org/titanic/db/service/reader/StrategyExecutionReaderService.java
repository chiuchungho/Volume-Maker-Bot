package org.titanic.db.service.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.titanic.db.entity.StrategyExecutionEntity;
import org.titanic.db.gateway.StrategyExecutionReader;
import org.titanic.db.repository.StrategyExecutionJpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Hanno Skowronek
 */
@Service
@RequiredArgsConstructor
public class StrategyExecutionReaderService  implements StrategyExecutionReader {

    private final StrategyExecutionJpaRepository strategyExecutionJpaRepository;

    @Override
    public List<StrategyExecutionEntity> getExecutionsOfStrategy(int strategyId) {
        return strategyExecutionJpaRepository.findAllByStrategy_Id(strategyId);
    }

    @Override
    public Optional<StrategyExecutionEntity> getLatestExecutionOfStrategy(int strategyId) {
        return strategyExecutionJpaRepository.findFirstByStrategy_IdOrderByStartTimeDesc(strategyId);
    }
}
