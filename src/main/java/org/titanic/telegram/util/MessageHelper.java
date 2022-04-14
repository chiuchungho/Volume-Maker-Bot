package org.titanic.telegram.util;


import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
public final class MessageHelper {

    public static SendMessage sendMessage(Message receivedMessage, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(receivedMessage.getChatId().toString());
        sendMessage.setText(escapeMessageForMarkdown(text));
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }

    public static SendMessage sendReplyMessage(Message receivedMessage, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(receivedMessage.getChatId().toString());
        sendMessage.setReplyToMessageId(receivedMessage.getMessageId());
        sendMessage.setText(escapeMessageForMarkdown(text));
        return sendMessage;
    }

    public static SendMessage sendMessageToChannel(String text, String channelId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(channelId);
        sendMessage.setText(escapeMessageForMarkdown(text));
        sendMessage.enableMarkdown(true);
        return sendMessage;
    }

    private static String escapeMessageForMarkdown(String text){
        return text = text
                .replace("_", "\\_")
//                .replace("*", "\\*")
                .replace("[", "\\[")
                .replace("`", "\\`");
    }

}
