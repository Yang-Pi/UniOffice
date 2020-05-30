package main.bot.utils.commands;

import main.bot.utils.BotUtils;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class ListGroups {
    public static InlineKeyboardMarkup buildGroupsInlineKeyboard(List<BotUtils.Group> groupList) {
        List<List<InlineKeyboardButton>> lineButtons = new ArrayList<>();
        List<InlineKeyboardButton> rowButtons = new ArrayList<>();

        for (int i = 0; i < groupList.size(); ++i) {
            String groupName = groupList.get(i).getName();
            Integer pos = groupName.indexOf('.');
            if (pos != -1) {
                groupName = groupName.substring(0, pos) + "/" + groupName.substring(pos + 1);
            }
            rowButtons.add(new InlineKeyboardButton().setText(groupName)
                    .setCallbackData("listgroups#" + groupList.get(i).getId() + "%" + groupList.get(i).getName()));

            if (i % 3 == 0 && (i != 0) || i == groupList.size() - 1) {
                lineButtons.add(rowButtons);
                rowButtons = new ArrayList<>();
            }
        }

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(lineButtons);

        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup buildPeopleInlineKeyboard(List<BotUtils.Person> peopleList, Integer groupId) {
        List<List<InlineKeyboardButton>> lineButtons = new ArrayList<>();
        List<InlineKeyboardButton> rowButtons = new ArrayList<>();

        rowButtons.add(new InlineKeyboardButton().setText("Назад").setCallbackData("backto#listgroups"));
        lineButtons.add(rowButtons);
        rowButtons = new ArrayList<>();

        if (groupId != 166) { //id of professors group
            rowButtons.add(new InlineKeyboardButton().setText("Успеваемость группы").setCallbackData("groupprogress#" + groupId));
            lineButtons.add(rowButtons);
            rowButtons = new ArrayList<>();
        }

        for (int i = 0; i < peopleList.size(); ++i) {
            String personInfo = peopleList.get(i).getLastName() + " " + peopleList.get(i).getFirstName() + " " + peopleList.get(i).getFatherName();
            rowButtons.add(new InlineKeyboardButton().setText(personInfo).setCallbackData("person#" + peopleList.get(i).getId()));

            if (i % 3 == 0 && (i != 0) || i == peopleList.size() - 1) {
                lineButtons.add(rowButtons);
                rowButtons = new ArrayList<>();
            }
        }

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(lineButtons);

        return keyboardMarkup;
    }
}
