package main.bot.utils.commands;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class AddGroup {
    private static String _name = "";
    private static Integer _messageId;

    public static String getName() {
        return _name;
    }

    public static void setName(String _name) {
        AddGroup._name = _name;
    }

    public static Integer getMessageId() {
        return _messageId;
    }

    public static void setMessageId(Integer _messageId) {
        AddGroup._messageId = _messageId;
    }

    public static InlineKeyboardMarkup buildAddButton() {
        List<List<InlineKeyboardButton>> lineButtons = new ArrayList<>();
        List<InlineKeyboardButton> rowButtons = new ArrayList<>();

        rowButtons.add(new InlineKeyboardButton().setText("Отмена").setCallbackData("addgroup_cancel"));
        rowButtons.add(new InlineKeyboardButton().setText("Добавить").setCallbackData("addgroup_add"));
        lineButtons.add(rowButtons);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(lineButtons);

        return keyboardMarkup;
    }
}
