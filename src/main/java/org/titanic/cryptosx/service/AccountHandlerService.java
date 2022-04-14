package org.titanic.cryptosx.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.titanic.cryptosx.dto.CryptosxAccount;
import org.titanic.cryptosx.gateway.AccountHandler;
import org.titanic.db.entity.AccountEntity;
import org.titanic.db.gateway.AccountWriter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;

/**
 * @author Hanno Skowronek
 */
@Service
@RequiredArgsConstructor
public class AccountHandlerService implements AccountHandler {

    //account 1
    @Value("${cryptosx.api.a1.key}")
    private String apiKey1;
    @Value("${cryptosx.api.a1.signature}")
    private String signature1;
    @Value("${cryptosx.api.a1.userId}")
    private String userId1;
    @Value("${cryptosx.api.a1.nonce}")
    private String nonce1;
    @Value("${cryptosx.api.a1.accountId}")
    private int accountId1;
    @Value("${cryptosx.api.a1.accountName}")
    private String accountName1;

    //account 2
    @Value("${cryptosx.api.a2.key}")
    private String apiKey2;
    @Value("${cryptosx.api.a2.signature}")
    private String signature2;
    @Value("${cryptosx.api.a2.userId}")
    private String userId2;
    @Value("${cryptosx.api.a2.nonce}")
    private String nonce2;
    @Value("${cryptosx.api.a2.accountId}")
    private int accountId2;
    @Value("${cryptosx.api.a2.accountName}")
    private String accountName2;

    private final List<CryptosxAccount> accounts;

    private final AccountWriter accountWriter;

    @PostConstruct
    private void init() {
        CryptosxAccount account1 = new CryptosxAccount(apiKey1, signature1, userId1, nonce1, accountId1, accountName1);
        CryptosxAccount account2 = new CryptosxAccount(apiKey2, signature2, userId2, nonce2, accountId2, accountName2);

        account2.setSubscribedToLevel2(true);

        accounts.add(account1);
        accounts.add(account2);

        accounts.forEach(account -> accountWriter.saveAccount(this.mapToEntity(account)));
    }

    @PreDestroy
    private void shutdown() {
        for (CryptosxAccount account : accounts) {
            account.disconnect();
        }
    }
    @Override
    public Optional<CryptosxAccount> getAccount(int userId) {
        return accounts.stream().filter(a -> a.getUserId().equals("" + userId)).findAny();
    }

    @Override
    public Optional<CryptosxAccount> getIdleAccount() {
        return accounts.stream().filter(CryptosxAccount::isIdle).findAny();
    }

    @Override
    public List<CryptosxAccount> getAccounts(int n) {
        return accounts.stream().limit(n).toList();
    }

    @Override
    public List<CryptosxAccount> getAccounts() {
        return accounts;
    }

    @Override
    public Optional<CryptosxAccount> getCorrespondingDto(AccountEntity accountEntity) {
        return accounts.stream().filter(acc -> acc.getUserId().equals(accountEntity.getUserId())).findAny();
    }

    private AccountEntity mapToEntity(CryptosxAccount account) {
        return AccountEntity.builder().userId(account.getUserId()).apiKey(account.getApiKey()).signature(account.getSignature()).nonce(account.getNonce()).accountName(account.getAccountName()).build();
    }
}
