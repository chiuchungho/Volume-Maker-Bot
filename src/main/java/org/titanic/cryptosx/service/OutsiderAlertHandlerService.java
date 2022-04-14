package org.titanic.cryptosx.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.titanic.cryptosx.dto.message.answer.Level2DataEvent;
import org.titanic.cryptosx.dto.message.answer.OrderStateEvent;
import org.titanic.cryptosx.gateway.OutsiderAlertHandler;
import org.titanic.enums.Instrument;
import org.titanic.enums.Side;
import org.titanic.db.gateway.TransactionReader;
import org.titanic.report.ReportService;

import java.util.List;

/**
 * @author Hanno Skowronek
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OutsiderAlertHandlerService implements OutsiderAlertHandler {

    private final TransactionReader transactionReader;
    private final ReportService reportService;

    @Override
    public void level2Update(List<Level2DataEvent> level2DataList) {
        for (Level2DataEvent level2Data : level2DataList) {
            if (!transactionReader.existsByAcceptedPriceQuantitySymbol(level2Data.getPrice(), level2Data.getQuantity(), Instrument.fromInteger(level2Data.getProductPairCode()).toString())) {
                log.warn("Outsider activity has been found, this is unexpected: {}", level2Data);
                reportService.createOutsiderOrderAlert(Instrument.MAMIUSDT.toString(), level2Data.getQuantity(), level2Data.getPrice(), Side.fromInteger(level2Data.getSide()), level2Data.getActionType());
            }
        }
    }
}
//    @Override
//    public List<TransactionEntity> tradeUpdate(List<TradeDataUpdateAnswer> tradeDataUpdateList) {
//        try {
//            Thread.sleep(30000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        ArrayList<TransactionEntity> transactionsToBeCancelled = new ArrayList<>();
//        for (TradeDataUpdateAnswer trade : tradeDataUpdateList) {
//            Optional<TransactionEntity> transactionOptional1 = transactionReader.getByOrderId(trade.getOrder1());
//            Optional<TransactionEntity> transactionOptional2 = transactionReader.getByOrderId(trade.getOrder2());
//
//            if (transactionOptional1.isEmpty()) {
//                if (transactionOptional2.isEmpty()) {
//                    log.warn("Outsider activity! A trade has been made between outsiders: {}", trade);
//                } else {
//                    if (transactionOptional2.get().getSide() == Side.Sell) {
//                        log.warn("Outsider activity! Someone else bought our coin: {}", trade);
//                        reportService.createOutsiderTradeAlert(Instrument.MAMIUSDT.toString(), trade.getQuantity(), trade.getPrice(), Side.Buy);
//                    } else {
//                        log.warn("Outsider activity! Someone else sold their coin to us: {}", trade);
//                        reportService.createOutsiderTradeAlert(Instrument.MAMIUSDT.toString(), trade.getQuantity(), trade.getPrice(), Side.Sell);
//                    }
//                    transactionsToBeCancelled.add(transactionOptional2.get());
//                }
//            } else {
//                if (transactionOptional2.isEmpty()) {
//                    if (transactionOptional1.get().getSide() == Side.Sell) {
//                        log.warn("Outsider activity! Someone else bought our coin: {}", trade);
//                        reportService.createOutsiderTradeAlert(Instrument.MAMIUSDT.toString(), trade.getQuantity(), trade.getPrice(), Side.Buy);
//                    } else {
//                        log.warn("Outsider activity! Someone else sold their coin to us: {}", trade);
//                        reportService.createOutsiderTradeAlert(Instrument.MAMIUSDT.toString(), trade.getQuantity(), trade.getPrice(), Side.Sell);
//                    }
//                    transactionsToBeCancelled.add(transactionOptional1.get());
//                }
//            }
//        }
//
//        return transactionsToBeCancelled;
//    }

