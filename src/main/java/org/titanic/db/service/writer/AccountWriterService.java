package org.titanic.db.service.writer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.titanic.db.entity.AccountEntity;
import org.titanic.db.gateway.AccountWriter;
import org.titanic.db.repository.AccountJpaRepository;

/**
 * @author Hanno Skowronek
 */
@Service
@RequiredArgsConstructor
public class AccountWriterService implements AccountWriter {

    private final AccountJpaRepository accountJpaRepository;

    @Override
    public String saveAccount(AccountEntity account) {
        return accountJpaRepository.save(account).getUserId();
    }
}
