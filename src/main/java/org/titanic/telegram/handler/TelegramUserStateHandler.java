package org.titanic.telegram.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
interface TelegramUserStateHandler {
    SendMessage handleInputConfirmStopByIdState(Message message, String username);
    SendMessage handleInputStopByIdState(Message message, String username);
    SendMessage handleInputConfirmStopAllState(Message message, String username);
    SendMessage handleInputConfirmCreateState(Message message, String username);
    SendMessage handleInputDurationHourMaxState(Message message, String username);
    SendMessage handleInputDurationHourMinState(Message message, String username);
    SendMessage handleInputPriceMinState(Message message, String username);
    SendMessage handleInputPriceMaxState(Message message, String username);
    SendMessage handleInputVolumeState(Message message, String username);
    SendMessage handleInputSymbolState(Message message, String username);
    SendMessage handleInputNumberOfTradesMinState(Message message, String username);
    SendMessage handleInputNumberOfTradesMaxState(Message message, String username);
}
