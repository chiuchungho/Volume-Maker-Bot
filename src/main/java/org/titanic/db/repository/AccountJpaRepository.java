package org.titanic.db.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.titanic.db.entity.AccountEntity;

/**
 * @author Hanno Skowronek
 */
@Repository
public interface AccountJpaRepository extends JpaRepository<AccountEntity, String> {
}
