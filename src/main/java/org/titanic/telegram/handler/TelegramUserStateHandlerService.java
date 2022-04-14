package org.titanic.telegram.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.titanic.db.entity.StrategyEntity;
import org.titanic.db.gateway.StrategyReader;
import org.titanic.db.gateway.StrategyWriter;
import org.titanic.scheduling.StrategyScheduler;
import org.titanic.telegram.util.MessageHelper;
import org.titanic.telegram.util.UserState;
import org.titanic.util.MathHelper;

import java.time.Instant;
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

    @NotNull
    public SendMessage handleInputConfirmStopByIdState(Message message, String username) {
        if(message.getText().equals("CONFIRM")){
            log.info("debug id: "+ UserState.getStopById(username));
            strategyScheduler.deactivateStrategy(UserState.getStopById(username));
            String response = "Stopped strategy ID: "+UserState.getStopById(username);
            UserState.removeUserState(username);
            return MessageHelper.sendReplyMessage(message, response);
        }
        return MessageHelper.sendReplyMessage(message, "Unrecognised input " + message.getText() + ".\nPlease type \"CONFIRM\" to stop all bot");
    }

    @NotNull
    public SendMessage handleInputStopByIdState(Message message, String username) {
        int id;
        try {
            id = Integer.parseInt(message.getText());
            Optional<StrategyEntity> strategy = strategyReader.getById(id);
            if (strategy.isPresent()) {
                UserState.setStopById(username, id);
                UserState.setUserState(username, UserState.INPUT_CONFIRM_STOP_BY_ID_STATE);
                return MessageHelper.sendMessage(message, "Please type *CONFIRM* to confirm stop strategy ID: "+message.getText());
            }
            return MessageHelper.sendMessage(message, "Invalid input: strategy ID="+message.getText()+" does not exist.\nPlease input again");
        }
        catch (NumberFormatException e) {
            return MessageHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nPlease enter integer (e.g. 1, 2, 3)");
        }
    }

    @NotNull
    public SendMessage handleInputConfirmStopAllState(Message message, String username) {
        if(message.getText().equals("CONFIRM")){
            strategyScheduler.deactivateAllStrategies();
            UserState.removeUserState(username);
            return MessageHelper.sendReplyMessage(message, "Stopped all bot");
        }
        return MessageHelper.sendReplyMessage(message, "Unrecognised input " + message.getText() + ".\nPlease type \"CONFIRM\" to stop all bot");
    }

    @NotNull
    public SendMessage handleInputConfirmCreateState(Message message, String username) {
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
            strategyScheduler.addStrategy(s);

            UserState.removeUserState(username);
            return MessageHelper.sendReplyMessage(message, "Confirmed");
        }
        return MessageHelper.sendReplyMessage(message, "Unrecognised input " + message.getText() + ". \n" +
                "Please type *CONFIRM* to confirm the trading pair for \n"+
                "Symbol:"+UserState.getSymbol(username)+ "\n" +
                "Volume: "+UserState.getVolume(username)+"\n" +
                "Min duration hour: "+UserState.getDurationHourMin(username)+"\n" +
                "Max duration hour: "+ UserState.getDurationHourMax(username)+"\n" +
                "Min Price: "+UserState.getPriceMin(username)+"\n" +
                "Max Price: "+ UserState.getPriceMax(username));
    }

    @NotNull
    public SendMessage handleInputDurationHourMaxState(Message message, String username) {
        int time;
        try {
            time = Integer.parseInt(message.getText());
            if(time < 1 || time >24){
                return MessageHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nmax duration hour must be between 1-24");
            }
            if(time < UserState.getDurationHourMin(username)){
                return MessageHelper.sendReplyMessage(message, "Invalid input: " + message.getText() +
                        "  Max duration can not be smaller than min hour duration="+UserState.getDurationHourMin(username));
            }
            UserState.setDurationHourMax(username, time);
            UserState.setUserState(username, UserState.INPUT_PRICE_MIN_STATE);
            return MessageHelper.sendMessage(message, "Please input min price e.g. 10.01");
        }
        catch (NumberFormatException e) {
            return MessageHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nPlease enter max duration hour again (e.g. 1-24)");
        }
    }

    @NotNull
    public SendMessage handleInputDurationHourMinState(Message message, String username) {
        int time;
        try {
            time = Integer.parseInt(message.getText());
            if(time < 1 || time >24) {
                return MessageHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nmin duration hour must be between 1-24");
            }
            UserState.setDurationHourMin(username, time);
            UserState.setUserState(username, UserState.INPUT_DURATION_HOUR_MAX_STATE);
            return MessageHelper.sendMessage(message, "Please enter max duration hour (e.g. 1-24)");
        }
        catch (NumberFormatException e) {
            return MessageHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nPlease enter min duration hour again (e.g. 1-24)");
        }
    }

    @NotNull
    public SendMessage handleInputPriceMinState(Message message, String username) {
        double price;
        try {
            price = Double.parseDouble(message.getText());
            if(price <= 0) {
                return MessageHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nmin price can not be 0 or smaller than 0");
            }
            UserState.setPriceMin(username, MathHelper.round(price,5));
            UserState.setUserState(username, UserState.INPUT_PRICE_MAX_STATE);
            return MessageHelper.sendMessage(message, "Please enter max price e.g. 13.12");
        }
        catch (NumberFormatException e) {
            return MessageHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nPlease input min price e.g. 10.01 again (e.g. 0-24)");
        }
    }

    @NotNull
    public SendMessage handleInputPriceMaxState(Message message, String username) {
        double price;
        try {
            price = Double.parseDouble(message.getText());
            if(price <= 0) {
                return MessageHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "  max price can not be 0 or smaller than 0");
            }
            if(price < UserState.getPriceMin(username)){
                return MessageHelper.sendReplyMessage(message, "Invalid input: " + message.getText() +
                        "\nMax price can not be smaller than min price="+UserState.getPriceMin(username));
            }
            UserState.setPriceMax(username, MathHelper.round(price,5));
            UserState.setUserState(username, UserState.INPUT_CONFIRM_CREATE_STATE);

            return MessageHelper.sendMessage(message,
                    "Please type *CONFIRM* to confirm the trading pair for \n"+
                            "Symbol:"+UserState.getSymbol(username)+ "\n" +
                            "Volume: "+UserState.getVolume(username)+"\n" +
                            "Min duration hour: "+UserState.getDurationHourMin(username)+"\n" +
                            "Max duration hour: "+ UserState.getDurationHourMax(username)+"\n" +
                            "Min price: "+UserState.getPriceMin(username)+"\n" +
                            "Max price hour: "+ UserState.getPriceMax(username));
        }
        catch (NumberFormatException e) {
            return MessageHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nPlease enter max price e.g. 13.12 again");
        }
    }


    @NotNull
    public SendMessage handleInputVolumeState(Message message, String username) {
        int volume;
        try {
            volume = Integer.parseInt(message.getText());
            UserState.setVolume(username, volume);
            UserState.setUserState(username, UserState.INPUT_DURATION_HOUR_MIN_STATE);
            return MessageHelper.sendMessage(message, "Please enter min duration (e.g. 1-24)");
        }
        catch (NumberFormatException e) {
            return MessageHelper.sendReplyMessage(message, "Invalid input: " + message.getText() + "\nPlease enter integer trading volume again  in the amount of token - number of MAMI (e.g. 0-2147483647)");
        }
    }

    @NotNull
    public SendMessage handleInputSymbolState(Message message, String username) {
        //need connect to websocket get all symbol and compare
        if(message.getText().equals("MAMIUSDT")){
            UserState.setSymbol(username,"MAMIUSDT");
            UserState.setUserState(username, UserState.INPUT_VOLUME_STATE);
            return MessageHelper.sendMessage(message, "Please enter integer trading volume for " + message.getText() + " in the amount of token - number of MAMI (e.g. 0-2147483647)");
        }
        return MessageHelper.sendReplyMessage(message, "Unrecognised symbol " + message.getText() + ".\nPlease input again");
    }
}
