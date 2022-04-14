package org.titanic.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.titanic.db.entity.StrategyExecutionEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author Hanno Skowronek
 */
@Repository
public interface StrategyExecutionJpaRepository extends JpaRepository<StrategyExecutionEntity, Integer> {

    List<StrategyExecutionEntity> findAllByStrategy_Id(int strategyId);

    Optional<StrategyExecutionEntity> findFirstByStrategy_IdOrderByStartTimeDesc(int strategyId);
}
