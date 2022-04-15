package org.titanic.telegram.handler;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.titanic.telegram.listener.TelegramBotListener;

import java.io.Serializable;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
public interface TelegramCommandHandler {
    void handleListAllActive(Message message, String username, TelegramBotListener telegramBotListener);
    void handleCreate(Message message, String username, TelegramBotListener telegramBotListener);
    void handleRemoveUserState(Message message, String username, TelegramBotListener telegramBotListener);
    void handleConfirmStopAllState(Message message, String username, TelegramBotListener telegramBotListener);
    void handleStopByIDState(Message message, String username, TelegramBotListener telegramBotListener);
    void handleListTodayTransactionsByID(Message message, String username, TelegramBotListener telegramBotListener);
}
