package org.titanic.telegram.handler;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.titanic.telegram.listener.TelegramBotListener;

import java.util.List;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
public interface TelegramMessageHandler {

    boolean checkAuthorizeByUsername(Update update, List<String> approvedTelegramUser);

    void handleIncomingMessage(Message message, TelegramBotListener telegramBotListener);
}
