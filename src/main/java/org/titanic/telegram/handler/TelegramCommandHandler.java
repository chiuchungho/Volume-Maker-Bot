package org.titanic.telegram.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
public interface TelegramCommandHandler {
    SendMessage handleListAllActive(Message message, String username);
    SendMessage handleCreate(Message message, String username);
    SendMessage handleRemoveUserState(Message message, String username);
    SendMessage handleConfirmStopAllState(Message message, String username);
    SendMessage handleStopByIDState(Message message, String username);
}
