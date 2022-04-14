package org.titanic.cryptosx.gateway;

import org.titanic.cryptosx.dto.CryptosxAccount;
import org.titanic.db.entity.AccountEntity;

import java.util.List;
import java.util.Optional;

/**
 * @author Hanno Skowronek
 */
public interface AccountHandler {

    Optional<CryptosxAccount> getAccount(int userId);

    Optional<CryptosxAccount> getIdleAccount();

    List<CryptosxAccount> getAccounts(int n);

    List<CryptosxAccount> getAccounts();

    Optional<CryptosxAccount> getCorrespondingDto(AccountEntity account);

}
