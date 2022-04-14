package org.titanic.db.service.writer;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.titanic.db.entity.StrategyEntity;
import org.titanic.db.gateway.StrategyWriter;
import org.titanic.db.repository.StrategyJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Hanno Skowronek
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class StrategyWriterService implements StrategyWriter {

    private final StrategyJpaRepository strategyJpaRepository;

    @Override
    public StrategyEntity saveStrategy(StrategyEntity strategy) {
        strategy = strategyJpaRepository.save(strategy);
        return strategy;
    }

    @Override
    public void deactivateAll() {
        List<StrategyEntity> entities = strategyJpaRepository.findAll();

        for (StrategyEntity strategyEntity : entities) {
            strategyEntity.setActive(false);
        }
        strategyJpaRepository.saveAll(entities);
    }

    @Override
    public void deactivateById(int strategyId) {
        Optional<StrategyEntity> strategy = strategyJpaRepository.findById(strategyId);

        if (strategy.isPresent()) {
            strategy.get().setActive(false);
            strategyJpaRepository.save(strategy.get());
        } else {
            log.info("No strategy with the id {} could be found for deactivation.", strategyId);
        }
    }

    @Override
    public Optional<StrategyEntity> activateById(int strategyId) {
        Optional<StrategyEntity> strategy = strategyJpaRepository.findById(strategyId);

        if (strategy.isPresent()) {
            strategy.get().setActive(true);
            return Optional.of(strategyJpaRepository.save(strategy.get()));
        } else {
            log.info("No strategy with the id {} could be found for activation.", strategyId);
            return Optional.empty();
        }
    }
}
