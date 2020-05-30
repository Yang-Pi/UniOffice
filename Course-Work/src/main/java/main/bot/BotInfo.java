package main.bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class BotInfo {
    private static final String _botUsername = "UniOfficeBot";
    private static final String _botToken = "1207659984:AAGvsB0VpuJm17DFEtfagjMikjjHr3Z16v0";
    private static final long _botChatId = 215758033;

    public static void initBot() throws TelegramApiRequestException {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        //Set up proxy
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
        botOptions.setProxyHost("177.87.39.104");
        botOptions.setProxyPort(3128);
        botOptions.setProxyType(DefaultBotOptions.ProxyType.HTTP);

        telegramBotsApi.registerBot(new Bot(botOptions));
    }

    public static String getBotUsername() {
        return _botUsername;
    }

    public static String getBotToken() {
        return _botToken;
    }

    public static long getChatId() {
        return _botChatId;
    }
}
