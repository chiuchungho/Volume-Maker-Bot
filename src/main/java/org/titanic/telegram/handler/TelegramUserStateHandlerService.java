package org.titanic.telegram.handler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.titanic.db.entity.StrategyEntity;
import org.titanic.db.entity.TransactionEntity;
import org.titanic.db.gateway.StrategyReader;
import org.titanic.db.gateway.TransactionReader;
import org.titanic.report.ReportService;
import org.titanic.scheduling.StrategyScheduler;
import org.titanic.telegram.TelegramService;
import org.titanic.telegram.listener.TelegramBotListener;
import org.titanic.telegram.util.SendHelper;
import org.titanic.telegram.util.UserState;
import org.titanic.util.MathHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramUserStateHandlerService implements TelegramUserStateHandler{
    private final StrategyReader strategyReader;
    private final StrategyScheduler strategyScheduler;
    private final TransactionReader transactionReader;
    private final ReportService reportService;

    @SneakyThrows
    @NotNull
    public void handleInputConfirmStopByIdState(Message message, String username, TelegramBotListener telegramBotListener) {
        if(message.getText().equals("CONFIRM")){
            log.info("debug id: "+ UserState.getStopById(username));
            strategyScheduler.deactivateStrategy(UserState.getStopById(username));
            String response = "Stopped strategy ID: "+UserState.getStopById(username);
            UserState.removeUserState(username);
            telegramBotListener.execute(SendHelper.sendReplyMessage(message, response));
            return;
        }
        telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Unrecognised input " + message.getText() + ".\nPlease type \"CONFIRM\" to stop all bot"));
        return;
    }

    @SneakyThrows
    @NotNull
    public void handleInputStopByIdState(Message message, String username, TelegramBotListener telegramBotListener) {
        int id;
        try {
            id = Integer.parseInt(message.getText());
            Optional<StrategyEntity> strategy = strategyReader.getById(id);
            if (strategy.isPresent()) {
                UserState.setStopById(username, id);
                UserState.setUserState(username, UserState.INPUT_CONFIRM_STOP_BY_ID_STATE);
                telegramBotListener.execute(SendHelper.sendMessage(message, "Please type *CONFIRM* to confirm stop strategy ID: "+message.getText()));
                return;
            }
            telegramBotListener.execute(SendHelper.sendMessage(message, "Invalid input: strategy ID="+message.getText()+" does not exist.\nPlease input again"));
            return;
        }
        catch (NumberFormatException e) {
            telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nPlease enter integer (e.g. 1, 2, 3)"));
            return;
        }
    }

    @SneakyThrows
    @NotNull
    public void handleInputConfirmStopAllState(Message message, String username, TelegramBotListener telegramBotListener) {
        if(message.getText().equals("CONFIRM")){
            strategyScheduler.deactivateAllStrategies();
            UserState.removeUserState(username);
            telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Stopped all bot"));
            return;
        }
        telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Unrecognised input " + message.getText() + ".\nPlease type \"CONFIRM\" to stop all bot"));
        return;
    }

    @SneakyThrows
    @NotNull
    public void handleInputDurationHourMaxState(Message message, String username, TelegramBotListener telegramBotListener) {
        int time;
        try {
            time = Integer.parseInt(message.getText());
            if(time < 1 || time >24){
                telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nmax duration hour must be between 1-24"));
                return;
            }
            if(time < UserState.getDurationHourMin(username)){
                telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Invalid input: " + message.getText() +
                        "  Max duration can not be smaller than min hour duration="+UserState.getDurationHourMin(username)));
                return;
            }
            UserState.setDurationHourMax(username, time);
            UserState.setUserState(username, UserState.INPUT_PRICE_MIN_STATE);
            telegramBotListener.execute(SendHelper.sendMessage(message, "Please input min price e.g. 10.01"));
            return;
        }
        catch (NumberFormatException e) {
            telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nPlease enter max duration hour again (e.g. 1-24)"));
            return;
        }
    }

    @SneakyThrows
    @NotNull
    public void handleInputDurationHourMinState(Message message, String username, TelegramBotListener telegramBotListener) {
        int time;
        try {
            time = Integer.parseInt(message.getText());
            if(time < 1 || time >24) {
                telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nmin duration hour must be between 1-24"));
                return;
            }
            UserState.setDurationHourMin(username, time);
            UserState.setUserState(username, UserState.INPUT_DURATION_HOUR_MAX_STATE);
            telegramBotListener.execute(SendHelper.sendMessage(message, "Please enter max duration hour (e.g. 1-24)"));
            return;
        }
        catch (NumberFormatException e) {
            telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nPlease enter min duration hour again (e.g. 1-24)"));
            return;
        }
    }

    @SneakyThrows
    @NotNull
    public void handleInputVolumeState(Message message, String username, TelegramBotListener telegramBotListener) {
        int volume;
        try {
            volume = Integer.parseInt(message.getText());
            UserState.setVolume(username, volume);
            UserState.setUserState(username, UserState.INPUT_DURATION_HOUR_MIN_STATE);
            telegramBotListener.execute(SendHelper.sendMessage(message, "Please enter min duration (e.g. 1-24)"));
            return;
        }
        catch (NumberFormatException e) {
            telegramBotListener.execute(SendHelper.sendReplyMessage(message,
                    "Invalid input: " + message.getText() + "\n" +
                    "Please enter integer trading volume again  in the amount of token - number of MAMI (e.g. 0-2147483647)"));
            return;
        }
    }

    @SneakyThrows
    @NotNull
    public void handleInputSymbolState(Message message, String username, TelegramBotListener telegramBotListener) {
        //need connect to websocket get all symbol and compare

        if (strategyReader.getAllActiveStrategiesBySymbol(message.getText()).size() > 0){
            telegramBotListener.execute(SendHelper.sendReplyMessage(message,
                    "There is an active strategy with symbol: *" + message.getText() + "*.\n" +
                            "It can not have the more than 2 same symbol strategy.\n" +
                            "Please check it in /list_all_active"));
            return;
        }

        if(message.getText().equals("MAMIUSDT")){
            UserState.setSymbol(username,"MAMIUSDT");
            UserState.setUserState(username, UserState.INPUT_VOLUME_STATE);
            telegramBotListener.execute(SendHelper.sendMessage(message, "Please enter integer trading volume for " + message.getText() + " in the amount of token - number of MAMI (e.g. 0-2147483647)"));
            return;
        }
        telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Unrecognised symbol " + message.getText() + ".\n" +
                "Please input again"));
        return;
    }

    @SneakyThrows
    @NotNull
    public void handleInputPriceMinState(Message message, String username, TelegramBotListener telegramBotListener) {
        double price;
        try {
            price = Double.parseDouble(message.getText());
            if(price <= 0) {
                telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nmin price can not be 0 or smaller than 0"));
            }
            UserState.setPriceMin(username, MathHelper.round(price,5));
            UserState.setUserState(username, UserState.INPUT_PRICE_MAX_STATE);
            telegramBotListener.execute(SendHelper.sendMessage(message, "Please enter max price e.g. 13.12"));
            return;
        }
        catch (NumberFormatException e) {
            telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nPlease input min price e.g. 10.01 again (e.g. 0-24)"));
            return;
        }
    }

    @SneakyThrows
    @NotNull
    public void handleInputPriceMaxState(Message message, String username, TelegramBotListener telegramBotListener) {
        double price;
        try {
            price = Double.parseDouble(message.getText());
            if(price <= 0) {
                telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "  max price can not be 0 or smaller than 0"));
                return;
            }
            if(price < UserState.getPriceMin(username)){
                telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Invalid input: " + message.getText() +
                        "\nMax price can not be smaller than min price="+UserState.getPriceMin(username)));
                return;
            }
            UserState.setPriceMax(username, MathHelper.round(price,5));
            UserState.setUserState(username, UserState.INPUT_NUMBER_OF_TRADES_MIN_STATE);

            telegramBotListener.execute(SendHelper.sendMessage(message, "Please enter Min Number of trade e.g. 10-400"));
            return;
        }
        catch (NumberFormatException e) {
            telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nPlease enter max price e.g. 13.12 again"));
            return;
        }
    }

    @SneakyThrows
    @NotNull
    public void handleInputNumberOfTradesMinState(Message message, String username, TelegramBotListener telegramBotListener) {
        int numberOfTrades;
        try {
            numberOfTrades = Integer.parseInt(message.getText());
            if (numberOfTrades < 10 || numberOfTrades > 400){
                telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Invalid input: " + message.getText() +
                        "\nMin number of trade must be between 10 to 400"));
                return;
            }
            UserState.setNumberOfTradesMin(username, numberOfTrades);
            UserState.setUserState(username, UserState.INPUT_NUMBER_OF_TRADES_MAX_STATE);
            telegramBotListener.execute(SendHelper.sendMessage(message, "Please enter max number of trade (e.g. 10-400)"));
            return;
        }
        catch (NumberFormatException e) {
            telegramBotListener.execute(SendHelper.sendReplyMessage(message,
                    "Invalid input: " + message.getText() + "\n" +
                            "Please enter integer number of trades again (e.g. 10-400)"));
            return;
        }
    }

    @SneakyThrows
    @NotNull
    public void handleInputNumberOfTradesMaxState(Message message, String username, TelegramBotListener telegramBotListener) {
        int numberOfTrades;
        try {
            numberOfTrades = Integer.parseInt(message.getText());
            if (numberOfTrades < 10 || numberOfTrades > 400){
                telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Invalid input: " + message.getText() +
                        "\nMin number of trade must be between 10 to 400"));
                return;
            }
            if(numberOfTrades < UserState.getNumberOfTradesMin(username)){
                telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Invalid input: " + message.getText() +
                        "\nMax number of trades can not be smaller than number of trades="+UserState.getNumberOfTradesMin(username)));
                return;
            }
            UserState.setNumberOfTradesMax(username, numberOfTrades);
            UserState.setUserState(username, UserState.INPUT_CONFIRM_CREATE_STATE);

            telegramBotListener.execute(SendHelper.sendMessage(message,
                    "Please type *CONFIRM* to confirm the trading pair for \n"+
                            "Symbol:"+UserState.getSymbol(username)+ "\n" +
                            "Volume: "+UserState.getVolume(username)+"\n" +
                            "Min duration hour: "+UserState.getDurationHourMin(username)+"\n" +
                            "Max duration hour: "+ UserState.getDurationHourMax(username)+"\n" +
                            "Min price: "+UserState.getPriceMin(username)+"\n" +
                            "Max price hour: "+ UserState.getPriceMax(username)+"\n" +
                            "Min number of trades: " +UserState.getNumberOfTradesMin(username)+"\n" +
                            "Max number of trades: " +UserState.getNumberOfTradesMax(username)));
            return;
        }
        catch (NumberFormatException e) {
            telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nPlease enter max price e.g. 13.12 again"));
            return;
        }
    }

    @SneakyThrows
    @NotNull
    public void handleInputConfirmCreateState(Message message, String username, TelegramBotListener telegramBotListener) {
        if(message.getText().equals("CONFIRM")){

            StrategyEntity s = new StrategyEntity();
            s.setCreated(Instant.now());
            s.setSymbol(UserState.getSymbol(username));
            s.setVolume(UserState.getVolume(username));
            s.setDurationHourMin(UserState.getDurationHourMin(username));
            s.setDurationHourMax(UserState.getDurationHourMax(username));
            s.setPriceMin(UserState.getPriceMin(username));
            s.setPriceMax(UserState.getPriceMax(username));
            s.setActive(true);
            s.setTelegramUsername(username);
            s.setNumberOfTradesMin(UserState.getNumberOfTradesMin(username));
            s.setNumberOfTradesMax(UserState.getNumberOfTradesMax(username));
            strategyScheduler.addStrategy(s);

            UserState.removeUserState(username);
            telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Confirmed"));
            return;
        }
        telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Unrecognised input " + message.getText() + ". \n" +
                "Please type *CONFIRM* to confirm the trading pair for \n"+
                "Symbol:"+UserState.getSymbol(username)+ "\n" +
                "Volume: "+UserState.getVolume(username)+"\n" +
                "Min duration hour: "+UserState.getDurationHourMin(username)+"\n" +
                "Max duration hour: "+ UserState.getDurationHourMax(username)+"\n" +
                "Min Price: "+UserState.getPriceMin(username)+"\n" +
                "Max price hour: "+ UserState.getPriceMax(username)+"\n" +
                "Min number of trades: " +UserState.getNumberOfTradesMin(username)+"\n" +
                "Max number of trades: " +UserState.getNumberOfTradesMax(username)));
        return;
    }

    @SneakyThrows
    @NotNull
    public void handleInputListTransactionByIDState(Message message, String username, TelegramBotListener telegramBotListener) {
        int id;
        try {
            id = Integer.parseInt(message.getText());
            Optional<StrategyEntity> strategy = strategyReader.getById(id);
            if (strategy.isPresent() && strategy.get().isActive()) {
                List<TransactionEntity> transactionEntities = transactionReader.getAllTransactionsFromTodayBySymbol(strategy.get().getSymbol());
                if (!transactionEntities.isEmpty()) {
                    telegramBotListener.execute(SendHelper.sendDocument(message, "", reportService.generateTransactionReport(transactionEntities)));
                    UserState.removeUserState(username);
                    return;
                }
                telegramBotListener.execute(SendHelper.sendMessage(message, "No transaction for this strategy scheduled for today"));
                return;
            }
            telegramBotListener.execute(SendHelper.sendMessage(message, "Strategy ID: "+message.getText()+"Not found. Please input Strategy ID again (ID from /list_all_active)"));
            return;
        }
        catch (NumberFormatException e) {
            telegramBotListener.execute(SendHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nPlease enter integer (e.g. 1, 2, 3) (ID from /list_all_active)"));
            return;
        }
    }

}
