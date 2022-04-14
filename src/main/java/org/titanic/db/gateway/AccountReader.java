package org.titanic.db.gateway;

import org.titanic.db.entity.AccountEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Hanno Skowronek
 */
public interface AccountReader {

    Optional<AccountEntity> getAccount(String userId);

    List<AccountEntity> getAllAccounts();

    List<AccountEntity> getAccounts(Collection<String> userIds);
}
