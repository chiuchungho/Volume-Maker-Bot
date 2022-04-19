package org.titanic.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.titanic.telegram.handler.TelegramMessageHandler;
import org.titanic.telegram.listener.TelegramBotListener;
import org.titanic.telegram.util.SendHelper;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;

/**
 * @author Hanno Skowronek, Chung Ho Chiu
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramService {

    @Value("${telegram.botToken}")
    private  String botToken;
    @Value("${telegram.botUsername}")
    private String botUsername;
    @Value("${telegram.channelId}")
    private String channelId;
    @Value("${telegram.approvedTelegramUser}")
    private List<String> approvedTelegramUser;

    private TelegramBotsApi telegramBotsApi;

    private static TelegramBotListener telegramBotListener;

    private final TelegramMessageHandler telegramMessageHandler;

    @PostConstruct
    private void init() {
      try {
          log.info("Initialising the TelegramBotsApi..");
          telegramBotsApi =new TelegramBotsApi(DefaultBotSession.class);
//          // Register long polling bots. They work regardless type of TelegramBotsApi we are creating
          telegramBotListener = new TelegramBotListener(botUsername, botToken, channelId, approvedTelegramUser, telegramMessageHandler);
          telegramBotsApi.registerBot(telegramBotListener);
      } catch (Exception e) {
          log.error("failed to register telegram bot api: ",e);
      }
    }

    public void sendMessageToChannel(String message) throws TelegramApiException {
        telegramBotListener.execute(SendHelper.sendMessageToChannel(channelId, message));
    }

    public void sendDocumentToChannel(String message, File file) throws TelegramApiException {
        telegramBotListener.execute(SendHelper.sendDocumentToChannel(channelId, message, file));
    }

}
