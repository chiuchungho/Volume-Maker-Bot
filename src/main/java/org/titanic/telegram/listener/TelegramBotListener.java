package org.titanic.telegram.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.titanic.telegram.handler.TelegramMessageHandler;
import org.titanic.telegram.util.SendHelper;
import org.titanic.telegram.util.UserState;

import java.util.List;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
@Slf4j
@RequiredArgsConstructor
public class TelegramBotListener extends TelegramLongPollingBot {
    //bot link: t.me/titanic_trade_volumn_bot
    //channel link: https://t.me/+iHrtVvogVngwOGVl

    private final String botUsername;
    private final String botToken;
    private final String channelID;
    private final List<String> approvedTelegramUser;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public String getChannelID() {
        return channelID;
    }

    private final TelegramMessageHandler telegramMessageHandler;

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()){
            log.info("Incoming Message: " +  update.getMessage().getText() +
                    " By User: " + update.getMessage().getFrom().getUserName()+
                    " User State: "+UserState.getUserState(update.getMessage().getFrom().getUserName()));
            if(telegramMessageHandler.checkAuthorizeByUsername(update, approvedTelegramUser)){
                try {
                    telegramMessageHandler.handleIncomingMessage(update.getMessage(), this);
                } catch (Exception e) {
                    log.error("incoming message: " + update.getMessage().toString()+ "\n"+
                            "user: " + update.getMessage().getFrom().getUserName()+ "\n"+
                            "failed to handleIncomingMessage: " + e);
                }
            }else{
                try {
                    execute(SendHelper.sendReplyMessage(update.getMessage(),"unauthorized user"));
                } catch (TelegramApiException e) {
                    log.error("failed to execute sendReplyMessage for unauthorized user: ",e);
                }
            }
        }
    }
}
