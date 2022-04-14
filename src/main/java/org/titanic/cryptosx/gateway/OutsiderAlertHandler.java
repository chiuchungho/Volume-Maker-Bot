package org.titanic.cryptosx.gateway;

import org.titanic.cryptosx.dto.message.answer.Level2DataEvent;
import org.titanic.cryptosx.dto.message.answer.OrderStateEvent;
import org.titanic.cryptosx.dto.message.answer.TradeDataUpdateAnswer;
import org.titanic.db.entity.TransactionEntity;

import java.util.List;

/**
 * @author Hanno Skowronek
 */
public interface OutsiderAlertHandler {

    void level2Update(List<Level2DataEvent> level2DataList);

//    List<TransactionEntity> tradeUpdate(List<TradeDataUpdateAnswer> tradeDataUpdateList);
}
