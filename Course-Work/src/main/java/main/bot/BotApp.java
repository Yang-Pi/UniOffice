package main.bot;

import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class BotApp {
    public static void main(String[] args) {
        try {
            BotInfo.initBot();
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }
}
