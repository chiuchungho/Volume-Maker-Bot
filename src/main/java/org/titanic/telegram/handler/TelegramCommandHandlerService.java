package org.titanic.telegram.handler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.titanic.db.entity.StrategyEntity;
import org.titanic.db.entity.TransactionEntity;
import org.titanic.db.gateway.StrategyReader;
import org.titanic.db.gateway.TransactionReader;
import org.titanic.telegram.TelegramService;
import org.titanic.telegram.listener.TelegramBotListener;
import org.titanic.telegram.util.SendHelper;
import org.titanic.telegram.util.UserState;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramCommandHandlerService implements TelegramCommandHandler{

    private final StrategyReader strategyReader;

    @SneakyThrows
    @NotNull
    public void handleListAllActive(Message message, String username, TelegramBotListener telegramBotListener){
        List<StrategyEntity> strategies = strategyReader.getAllActiveStrategies();
        StringBuilder response;
        if (strategies.size() != 0) {
            response = new StringBuilder(String.format(
                    "%5s %5s %10s %5s %10s %4s %5s %2s %10s %3s %10s %3s %10s \n",
                    "ID", "|", "User", "|", "Symbol", "|", "Volume", "|", "Time", "|", "MinPrice", "|", "MaxPrice"));
            response.append("\n-------------------------------------");
            for (StrategyEntity strategy : strategies) {
                response.append(String.format(
                        "\n%3d %5s %5s %2s %5s %2s %.0f %2s %5s %2s %.2f %2s %.2f",
                        strategy.getId(), "|", strategy.getTelegramUsername(), "|", strategy.getSymbol(), "|", strategy.getVolume(), "|", strategy.getCreated().toString().substring(0,19) , "|", strategy.getPriceMin() , "|", strategy.getPriceMax()));
            }
            response.append("\n-------------------------------------");
            telegramBotListener.execute(SendHelper.sendMessage(message, response.toString()));
            return;
        }
        UserState.removeUserState(username);
        telegramBotListener.execute(SendHelper.sendMessage(message, "no active strategy"));
        return;
    }

    @SneakyThrows
    @NotNull
    public void handleCreate(Message message, String username, TelegramBotListener telegramBotListener){
        UserState.setUserState(username, UserState.INPUT_SYMBOL_STATE);
        telegramBotListener.execute(SendHelper.sendReplyMessage(message,"Please enter trading symbol (for example: MAMIUSDT)"));
        return;
    }

    @SneakyThrows
    @NotNull
    public void handleRemoveUserState(Message message, String username, TelegramBotListener telegramBotListener){
        UserState.removeUserState(username);
        telegramBotListener.execute(SendHelper.sendReplyMessage(message,"Stopped current input"));
        return;
    }

    @SneakyThrows
    @NotNull
    public void handleConfirmStopAllState(Message message, String username, TelegramBotListener telegramBotListener){
        UserState.setUserState(username, UserState.INPUT_CONFIRM_STOP_ALL_STATE);
        telegramBotListener.execute(SendHelper.sendReplyMessage(message,"Please type *CONFIRM* to stop all bot"));
        return;
    }

    @SneakyThrows
    @NotNull
    public void handleStopByIDState(Message message, String username, TelegramBotListener telegramBotListener){
        UserState.setUserState(username, UserState.INPUT_STOP_BY_ID_STATE);
        telegramBotListener.execute(SendHelper.sendReplyMessage(message,"Please input ID to stop strategy \n (ID from /list_all_active)"));
        return;
    }

    @SneakyThrows
    @NotNull
    public void handleListTodayTransactionsByID(Message message, String username, TelegramBotListener telegramBotListener){
        UserState.setUserState(username, UserState.INPUT_LIST_TRANSACTION_BY_ID_STATE);
        telegramBotListener.execute(SendHelper.sendMessage(message, "Please input Strategy ID (ID from /list_all_active)"));
        return;
    }

}
