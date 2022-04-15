package org.titanic.db.service.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.titanic.db.entity.StrategyEntity;
import org.titanic.db.gateway.StrategyReader;
import org.titanic.db.repository.StrategyJpaRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

/**
 * @author Hanno Skowronek
 */
@Service
@RequiredArgsConstructor
public class StrategyReaderService implements StrategyReader {

    private final StrategyJpaRepository strategyJpaRepository;

    @Override
    public List<StrategyEntity> getAllStrategies() {
        return strategyJpaRepository.findAll();
    }

    @Override
    public List<StrategyEntity> getAllActiveStrategies() {
        return strategyJpaRepository.findAllByActiveIsTrue();
    }

    @Override
    public Optional<StrategyEntity> getById(int strategyId) {
        return strategyJpaRepository.findById(strategyId);
    }

    @Override
    public List<StrategyEntity> getAllActiveStrategiesBySymbol(String symbol) {return strategyJpaRepository.findAllBySymbolAndActiveIsTrue(symbol); }

}
