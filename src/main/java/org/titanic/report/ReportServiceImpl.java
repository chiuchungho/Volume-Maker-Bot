package org.titanic.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.titanic.db.entity.StrategyExecutionEntity;
import org.titanic.db.entity.TransactionEntity;
import org.titanic.db.gateway.TransactionReader;
import org.titanic.enums.Side;
import org.titanic.telegram.TelegramService;
import org.titanic.telegram.util.Emoji;

import java.util.List;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TelegramService telegramService;

    private final TransactionReader transactionReader;

//    @Scheduled(cron = " 0 0 0 * * * ")
//    public void executeDailyReport(){
//        System.out.println("excuteDailyReport");
//        try {
//            telegramService.sendMessageToChannel("test message from cron job");
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void createAndExecuteReport(StrategyExecutionEntity strategyExecution) {
        try {
            StringBuilder response = new StringBuilder(Emoji.SPEAK_NO_EVIL_MONKEY +"Execution Report \n");
            response.append("\n-------------------------------------\n");
            response.append("ID: "+strategyExecution.getStrategy().getId()+" Symbol: "+strategyExecution.getStrategy().getSymbol()+" is done for today\n");
            telegramService.sendMessageToChannel(response.toString());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createLowCapitalAlert(String userEmail, String symbol, double balance) {
        try {
            telegramService.sendMessageToChannel(
                    Emoji.RAISED_HAND +"Lower Capital Alert!"+"\n"+
                            "Account: "+userEmail+" is in low balance\n" +
                            "Symbol: " + symbol +"\n"+
                            "Balance is at: "+balance);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createOutsiderOrderAlert(String symbol, double volume, double price, Side side, int actionType) {
        try {
            String message = "Outside Alert for ";
            switch (actionType) {
                case 0 -> message += "placing ";
                case 1 -> message += "updating ";
                case 2 -> message += "closing (cancelled/filled) ";
            }
            telegramService.sendMessageToChannel(
                    Emoji.RAISED_HAND + message + "an order!\n"+
                            "Symbol: "+symbol+"\n"+
                            "Volume: "+volume+"\n"+
                            "Price: "+price+"\n"+
                            "Side: "+side
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createOutsiderTradeAlert(String symbol, double volume, double price, Side side) {
        String message = Emoji.RAISED_HAND + "Outside Alert for ";
        if (side == Side.Sell) {
            message += "selling to us!";
        } else if (side == Side.Buy) {
            message += "buying from us!";
        }
        try {
            telegramService.sendMessageToChannel(
                    message + "\n" +
                            "Symbol: "+symbol+"\n"+
                            "Volume: "+volume+"\n"+
                            "Price: "+price+"\n"+
                            "Side: "+side
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
