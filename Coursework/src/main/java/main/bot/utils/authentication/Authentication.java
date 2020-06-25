package main.bot.utils.authentication;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class Authentication {
    public static InlineKeyboardMarkup buildInlineNumberKeyboard() {
        List<List<InlineKeyboardButton>> lineButtons = new ArrayList<>();
        List<InlineKeyboardButton> rowButtons = new ArrayList<>();

        for (Integer i = 1; i <= 9; ++i) {
            rowButtons.add(new InlineKeyboardButton().setText(i.toString()).setCallbackData("sign_password#" + i.toString()));
            if (i % 3 == 0) {
                lineButtons.add(rowButtons);
                rowButtons = new ArrayList<>();
            }
        }
        rowButtons.add(new InlineKeyboardButton().setText("Отмена").setCallbackData("sign_cancel"));
        rowButtons.add(new InlineKeyboardButton().setText("0").setCallbackData("sign_password#0"));
        rowButtons.add(new InlineKeyboardButton().setText("Готово").setCallbackData("sign_ready"));
        lineButtons.add(rowButtons);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(lineButtons);

        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup buildInlineStatusKeyboard() {
        List<List<InlineKeyboardButton>> lineButtons = new ArrayList<>();
        List<InlineKeyboardButton> rowButtons = new ArrayList<>();

        rowButtons.add(new InlineKeyboardButton().setText("Студент").setCallbackData("sign_status%STUDENT"));
        lineButtons.add(rowButtons);
        rowButtons = new ArrayList<>();

        rowButtons.add(new InlineKeyboardButton().setText("Преподаватель").setCallbackData("sign_status%PROFESSOR"));
        lineButtons.add(rowButtons);
        rowButtons = new ArrayList<>();

        rowButtons.add(new InlineKeyboardButton().setText("Менеджер").setCallbackData("sign_status%MANAGER"));
        lineButtons.add(rowButtons);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(lineButtons);

        return keyboardMarkup;
    }
}
