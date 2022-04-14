package org.titanic.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.titanic.db.entity.StrategyEntity;

import java.util.List;
import java.util.UUID;

/**
 * @author Hanno Skowronek
 */
@Repository
public interface StrategyJpaRepository extends JpaRepository<StrategyEntity, Integer> {

    List<StrategyEntity> findAllByActiveIsTrue();
}
