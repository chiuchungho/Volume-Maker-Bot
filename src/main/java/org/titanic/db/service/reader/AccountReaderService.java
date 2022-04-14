package org.titanic.db.service.reader;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.titanic.db.entity.AccountEntity;
import org.titanic.db.gateway.AccountReader;
import org.titanic.db.repository.AccountJpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Hanno Skowronek
 */
@Service
@RequiredArgsConstructor
public class AccountReaderService implements AccountReader {

    private final AccountJpaRepository accountRepository;

    @Override
    public Optional<AccountEntity> getAccount(String userId) {
        return accountRepository.findById(userId);
    }

    @Override
    public List<AccountEntity> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public List<AccountEntity> getAccounts(Collection<String> userIds) {
        return accountRepository.findAllById(userIds);
    }
}
