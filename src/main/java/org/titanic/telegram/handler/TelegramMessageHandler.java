package org.titanic.telegram.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
public interface TelegramMessageHandler {

    boolean checkAuthorizeByUsername(Update update, List<String> approvedTelegramUser);

    SendMessage handleIncomingMessage(Message message);
}
