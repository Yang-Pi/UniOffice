package main.bot.utils.commands;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class AddPerson {
    private static String _lastName;
    private static String _firstName;
    private static String _fatherName;

    private static boolean wasFirstFieldSet = false;
    private static boolean wasSecondFieldSet = false;

    private static String _groupId;
    private static String _groupType;

    private static Integer _messageIdGroups;
    private static Integer _messageIdToolsBar;

    public static String getGroupId() {
        return _groupId;
    }

    public static void setGroupId(String groupId) {
        AddPerson._groupId = groupId;
    }

    public static String getGroupType() {
        return _groupType;
    }

    public static void setGroupType(String _groupType) {
        AddPerson._groupType = _groupType;
    }

    public static Integer getMessageIdGroups() {
        return _messageIdGroups;
    }

    public static void setMessageIdGroups(Integer messageIdGroups) {
        AddPerson._messageIdGroups = messageIdGroups;
    }

    public static Integer getMessageIdToolsBar() {
        return _messageIdToolsBar;
    }

    public static void setMessageIdToolsBar(Integer messageIdToolsBar) {
        AddPerson._messageIdToolsBar = messageIdToolsBar;
    }

    public static String getLastName() {
        return _lastName;
    }

    public static void setLastName(String _lastName) {
        AddPerson._lastName = _lastName;
    }

    public static String getFirstName() {
        return _firstName;
    }

    public static void setFirstName(String _firstName) {
        AddPerson._firstName = _firstName;
    }

    public static String getFatherName() {
        return _fatherName;
    }

    public static void setFatherName(String _fatherName) {
        AddPerson._fatherName = _fatherName;
    }

    public static boolean wasFirstFieldSet() {
        return wasFirstFieldSet;
    }

    public static void setWasFirstFieldSet(boolean wasFirstFieldSet) {
        AddPerson.wasFirstFieldSet = wasFirstFieldSet;
    }

    public static boolean wasSecondFieldSet() {
        return wasSecondFieldSet;
    }

    public static void setWasSecondFieldSet(boolean wasSecondFieldSet) {
        AddPerson.wasSecondFieldSet = wasSecondFieldSet;
    }

    public static InlineKeyboardMarkup buildInlineSpecKeyboard() {
        List<List<InlineKeyboardButton>> lineButtons = new ArrayList<>();
        List<InlineKeyboardButton> rowButtons = new ArrayList<>();

        rowButtons.add(new InlineKeyboardButton().setText("Отмена").setCallbackData("addperson_cancel"));
        rowButtons.add(new InlineKeyboardButton().setText("Добавить").setCallbackData("addperson_add"));
        lineButtons.add(rowButtons);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(lineButtons);

        return keyboardMarkup;
    }
}
