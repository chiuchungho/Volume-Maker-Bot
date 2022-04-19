package org.titanic.report;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.titanic.db.entity.StrategyExecutionEntity;
import org.titanic.db.entity.TransactionEntity;
import org.titanic.db.gateway.TransactionReader;
import org.titanic.enums.Side;
import org.titanic.telegram.TelegramService;
import org.titanic.telegram.util.Emoji;

import java.io.File;
import java.nio.charset.Charset;
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

            List<TransactionEntity> transactionEntities = transactionReader.getAllTransactionsFromTodayBySymbol(strategyExecution.getStrategy().getSymbol());
            telegramService.sendDocumentToChannel(response.toString(), generateTransactionReport(transactionEntities));
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

    @Override
    @SneakyThrows
    public File generateTransactionReport(List<TransactionEntity> transactionEntities){
        StringBuilder sb = new StringBuilder(String.format(
                "%20s %2s %10s %2s %10s %2s %30s %2s %5s %2s %5s \n",
                "Time", "|", "Status", "|", "Side", "|", "Account", "|", "Price", "|", "Volume"));

        for (TransactionEntity transactionEntity : transactionEntities) {
            sb.append(String.format(
                    "%20s %2s %10s %2s %10s %2s %30s %2s %5.2f %2s %5.2f",
                    transactionEntity.getTimestamp().toString().substring(0,19), "|",
                    transactionEntity.getStatus(), "|",
                    transactionEntity.getSide() , "|",
                    transactionEntity.getAccount().getAccountName(),  "|",
                    transactionEntity.getPrice() ,  "|",
                    transactionEntity.getVolume()
            ));
        }

//        File file = File.createTempFile("./transaction-report-"+ transactionEntities.get(0).getSymbol(), ".txt");
//        file.deleteOnExit();
        File file = new File("./transaction-report-"+ transactionEntities.get(0).getSymbol()+".txt");
        FileUtils.writeStringToFile(file, sb.toString(), Charset.forName("UTF-8"));
        return file;
    }
}
