package org.titanic.telegram.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.titanic.telegram.listener.TelegramBotListener;
import org.titanic.telegram.util.Command;
import org.titanic.telegram.util.SendHelper;
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
    private final TelegramCommandHandler telegramCommandHandler;

    @Override
    public boolean checkAuthorizeByUsername(Update update, List<String> approvedTelegramUser){
        return approvedTelegramUser.contains(update.getMessage().getFrom().getUserName());
    }

    @Override
    public void handleIncomingMessage(Message message, TelegramBotListener telegramBotListener){
        String username = message.getFrom().getUserName();
        switch (message.getText()) {
            case Command.CREATE_STRATEGY -> telegramCommandHandler.handleCreate(message, username, telegramBotListener);
            case Command.CANCEL_INPUT -> telegramCommandHandler.handleRemoveUserState(message, username, telegramBotListener);
            case Command.STOP_ALL_STRATEGY -> telegramCommandHandler.handleConfirmStopAllState(message, username, telegramBotListener);
            case Command.STOP_STRATEGY_BY_ID -> telegramCommandHandler.handleStopByIDState(message, username, telegramBotListener);
            case Command.LIST_ALL_ACTIVE -> telegramCommandHandler.handleListAllActive(message, username, telegramBotListener);
            case Command.LIST_TODAY_TRANSACTIONS_BY_ID -> telegramCommandHandler.handleListTodayTransactionsByID(message, username, telegramBotListener);
            default -> handleUserState(message, username, telegramBotListener);
        };
    }

    private void handleUserState(Message message, String username, TelegramBotListener telegramBotListener){
        switch (UserState.getUserState(username)) {
            // "/create"
            case UserState.INPUT_SYMBOL_STATE -> telegramUserStateHandler.handleInputSymbolState(message, username, telegramBotListener);
            case UserState.INPUT_VOLUME_STATE -> telegramUserStateHandler.handleInputVolumeState(message, username, telegramBotListener);
            case UserState.INPUT_DURATION_HOUR_MIN_STATE -> telegramUserStateHandler.handleInputDurationHourMinState(message, username, telegramBotListener);
            case UserState.INPUT_DURATION_HOUR_MAX_STATE -> telegramUserStateHandler.handleInputDurationHourMaxState(message, username, telegramBotListener);
            case UserState.INPUT_PRICE_MIN_STATE -> telegramUserStateHandler.handleInputPriceMinState(message, username, telegramBotListener);
            case UserState.INPUT_PRICE_MAX_STATE -> telegramUserStateHandler.handleInputPriceMaxState(message, username, telegramBotListener);
            case UserState.INPUT_NUMBER_OF_TRADES_MIN_STATE -> telegramUserStateHandler.handleInputNumberOfTradesMinState(message, username, telegramBotListener);
            case UserState.INPUT_NUMBER_OF_TRADES_MAX_STATE -> telegramUserStateHandler.handleInputNumberOfTradesMaxState(message, username, telegramBotListener);
            case UserState.INPUT_CONFIRM_CREATE_STATE -> telegramUserStateHandler.handleInputConfirmCreateState(message, username, telegramBotListener);
            // "/stop_all"
            case UserState.INPUT_CONFIRM_STOP_ALL_STATE -> telegramUserStateHandler.handleInputConfirmStopAllState(message, username, telegramBotListener);
            // "/stop_by_id"
            case UserState.INPUT_STOP_BY_ID_STATE -> telegramUserStateHandler.handleInputStopByIdState(message, username, telegramBotListener);
            case UserState.INPUT_CONFIRM_STOP_BY_ID_STATE -> telegramUserStateHandler.handleInputConfirmStopByIdState(message, username, telegramBotListener);
            // "/list_today_transactions_by_id"
            case UserState.INPUT_LIST_TRANSACTION_BY_ID_STATE ->  telegramUserStateHandler.handleInputListTransactionByIDState(message, username, telegramBotListener);
            default -> SendHelper.sendReplyMessage(message, "unknown message or command - please type /create to start the bot");
        };
    }
}
