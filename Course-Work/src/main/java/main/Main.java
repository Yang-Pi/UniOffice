package main;

import main.bot.BotApp;
import main.server.SpringBootApp;

public class Main {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SpringBootApp.main(args);
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                BotApp.main(args);
            }
        }).start();
    }
}
