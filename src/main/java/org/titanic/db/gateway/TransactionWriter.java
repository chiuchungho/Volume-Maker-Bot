package org.titanic.db.gateway;

import org.titanic.db.entity.TransactionEntity;

/**
 * @author Hanno Skowronek
 */
public interface TransactionWriter {

    TransactionEntity saveTransaction(TransactionEntity transactionEntity);

}
