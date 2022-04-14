package org.titanic.db.gateway;

import org.titanic.db.entity.AccountEntity;

/**
 * @author Hanno Skowronek
 */
public interface AccountWriter {

    String saveAccount(AccountEntity account);
}
