package org.titanic.telegram.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.titanic.telegram.listener.TelegramBotListener;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
interface TelegramUserStateHandler {
    void handleInputConfirmStopByIdState(Message message, String username, TelegramBotListener telegramBotListener);
    void handleInputStopByIdState(Message message, String username, TelegramBotListener telegramBotListener);
    void handleInputConfirmStopAllState(Message message, String username, TelegramBotListener telegramBotListener);
    void handleInputConfirmCreateState(Message message, String username, TelegramBotListener telegramBotListener);
    void handleInputDurationHourMaxState(Message message, String username, TelegramBotListener telegramBotListener);
    void handleInputDurationHourMinState(Message message, String username, TelegramBotListener telegramBotListener);
    void handleInputPriceMinState(Message message, String username, TelegramBotListener telegramBotListener);
    void handleInputPriceMaxState(Message message, String username, TelegramBotListener telegramBotListener);
    void handleInputVolumeState(Message message, String username, TelegramBotListener telegramBotListener);
    void handleInputSymbolState(Message message, String username, TelegramBotListener telegramBotListener);
    void handleInputNumberOfTradesMinState(Message message, String username, TelegramBotListener telegramBotListener);
    void handleInputNumberOfTradesMaxState(Message message, String username, TelegramBotListener telegramBotListener);
}
