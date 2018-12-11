package me.exrates.service.notifications.telegram;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.exception.MessageUndeliweredException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.stream.Stream;


/**
 * Created by Maks on 05.10.2017.
 */
@PropertySource("classpath:telegram_bot.properties")
@Log4j2(topic = "message_notify")
@Component
public class TelegramBotService  extends TelegramLongPollingBot {

    private @Value("${telegram.bot.username}") String botName;

    static {ApiContextInitializer.init();}


    @PostConstruct
    private void init() {
        if (Stream.of("exrates_local_test_bot", "exrates_test_bot").noneMatch(p->p.equalsIgnoreCase(botName))) {
            log.debug("init telegram bot {}", botName);
            TelegramBotsApi botsApi = new TelegramBotsApi();
            try {
                botsApi.registerBot(this);
            } catch (TelegramApiException e) {
                log.error("error while initialize bot {}", e);
            }
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(text);
        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            throw new MessageUndeliweredException();
        }
    }


}
