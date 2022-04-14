package org.titanic.telegram.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.titanic.telegram.util.Command;
import org.titanic.telegram.util.MessageHelper;
import org.titanic.telegram.util.UserState;

import java.util.List;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TelegramMessageHandlerService implements TelegramMessageHandler {
    private final TelegramUserStateHandler telegramUserStateHandler;
    private  final TelegramCommandHandler telegramCommandHandler;

    @Override
    public boolean checkAuthorizeByUsername(Update update, List<String> approvedTelegramUser){
        return approvedTelegramUser.contains(update.getMessage().getFrom().getUserName());
    }

    @Override
    public SendMessage handleIncomingMessage(Message message){
        String username = message.getFrom().getUserName();
        return switch (message.getText()) {
            case Command.CREATE_STRATEGY -> telegramCommandHandler.handleCreate(message, username);
            case Command.CANCEL_INPUT -> telegramCommandHandler.handleRemoveUserState(message, username);
            case Command.STOP_ALL_STRATEGY -> telegramCommandHandler.handleConfirmStopAllState(message, username);
            case Command.STOP_STRATEGY_BY_ID -> telegramCommandHandler.handleStopByIDState(message, username);
            case Command.LIST_ALL_ACTIVE -> telegramCommandHandler.handleListAllActive(message, username);
            default -> handleUserState(message, username);
        };
    }

    private SendMessage handleUserState(Message message, String username){
        return switch (UserState.getUserState(username)) {
            // "/create"
            case UserState.INPUT_SYMBOL_STATE -> telegramUserStateHandler.handleInputSymbolState(message, username);
            case UserState.INPUT_VOLUME_STATE -> telegramUserStateHandler.handleInputVolumeState(message, username);
            case UserState.INPUT_DURATION_HOUR_MIN_STATE -> telegramUserStateHandler.handleInputDurationHourMinState(message, username);
            case UserState.INPUT_DURATION_HOUR_MAX_STATE -> telegramUserStateHandler.handleInputDurationHourMaxState(message, username);
            case UserState.INPUT_PRICE_MIN_STATE -> telegramUserStateHandler.handleInputPriceMinState(message, username);
            case UserState.INPUT_PRICE_MAX_STATE -> telegramUserStateHandler.handleInputPriceMaxState(message, username);
            case UserState.INPUT_CONFIRM_CREATE_STATE -> telegramUserStateHandler.handleInputConfirmCreateState(message, username);
            // "/stop_all"
            case UserState.INPUT_CONFIRM_STOP_ALL_STATE -> telegramUserStateHandler.handleInputConfirmStopAllState(message, username);
            // "/stop_by_id"
            case UserState.INPUT_STOP_BY_ID_STATE -> telegramUserStateHandler.handleInputStopByIdState(message, username);
            case UserState.INPUT_CONFIRM_STOP_BY_ID_STATE -> telegramUserStateHandler.handleInputConfirmStopByIdState(message, username);
            default -> MessageHelper.sendReplyMessage(message, "unknown message or command - please type /create to start the bot");
        };
    }
}
