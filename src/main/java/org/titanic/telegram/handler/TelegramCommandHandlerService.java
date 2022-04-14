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
import org.titanic.telegram.util.MessageHelper;
import org.titanic.telegram.util.UserState;

import java.util.List;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramCommandHandlerService implements TelegramCommandHandler{
    private final StrategyReader strategyReader;

    @NotNull
    public SendMessage handleListAllActive(Message message, String username){
        List<StrategyEntity> strategies = strategyReader.getAllActiveStrategies();
        StringBuilder response;
        if (strategies.size() != 0) {
            response = new StringBuilder(String.format("%5s %5s %10s %5s %10s %5s %10s %5s %10s \n", "ID", "|", "User", "|", "Symbol", "|", "Volume", "|", "Time"));
            response.append("\n-------------------------------------");
            for (StrategyEntity strategy : strategies) {
                response.append(String.format("\n%3d %5s %5s %2s %5s %2s %.0f %2s %5s", strategy.getId(), "|", strategy.getTelegramUsername(), "|", strategy.getSymbol(), "|", strategy.getVolume(), "|", strategy.getCreated().toString()));
            }
            response.append("\n-------------------------------------");
            return MessageHelper.sendMessage(message, response.toString());
        }
        UserState.removeUserState(username);
        return MessageHelper.sendMessage(message, "no active strategy");
    }

    @NotNull
    public SendMessage handleCreate(Message message, String username){
        UserState.setUserState(username, UserState.INPUT_SYMBOL_STATE);
        return MessageHelper.sendReplyMessage(message,"Please enter trading symbol (for example: MAMIUSDT)");
    }

    @NotNull
    public SendMessage handleRemoveUserState(Message message, String username){
        UserState.removeUserState(username);
        return MessageHelper.sendReplyMessage(message,"Stopped current input");
    }

    @NotNull
    public SendMessage handleConfirmStopAllState(Message message, String username){
        UserState.setUserState(username, UserState.INPUT_CONFIRM_STOP_ALL_STATE);
        return MessageHelper.sendReplyMessage(message,"Please type \"CONFIRM\" to stop all bot");
    }

    @NotNull
    public SendMessage handleStopByIDState(Message message, String username){
        UserState.setUserState(username, UserState.INPUT_STOP_BY_ID_STATE);
        return MessageHelper.sendReplyMessage(message,"Please input ID to stop strategy \n (ID from /ListAllActive)");
    }

}
