package org.titanic.db.service.writer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.titanic.db.entity.TransactionEntity;
import org.titanic.db.gateway.TransactionWriter;
import org.titanic.db.repository.TransactionJpaRepository;

/**
 * @author Hanno Skowronek
 */
@Service
@RequiredArgsConstructor
public class TransactionWriterService implements TransactionWriter {

    private final TransactionJpaRepository transactionJpaRepository;

    @Override
    public TransactionEntity saveTransaction(TransactionEntity transactionEntity) {
        return transactionJpaRepository.save(transactionEntity);
    }
}
