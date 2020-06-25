package main.bot.utils.commands;

import main.bot.utils.BotUtils;
import main.bot.utils.server.Server;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class ListPeople {
    public static InlineKeyboardMarkup buildStudentFunctionInlineKeyboard(Integer id) {
        List<List<InlineKeyboardButton>> lineButtons = new ArrayList<>();
        List<InlineKeyboardButton> rowButtons = new ArrayList<>();

        rowButtons.add(new InlineKeyboardButton().setText("Свернуть").setCallbackData("close"));
        lineButtons.add(rowButtons);
        rowButtons = new ArrayList<>();

        rowButtons.add(new InlineKeyboardButton().setText("Средний балл: " + computeAverageMark(id)).setCallbackData("personaverage#" + id));
        rowButtons.add(new InlineKeyboardButton().setText("Список оценок").setCallbackData("personmarks#" + id));

        lineButtons.add(rowButtons);
        rowButtons = new ArrayList<>();

        rowButtons.add(new InlineKeyboardButton().setText("Поставить оценку").setCallbackData("personaddmark#" + id));
        lineButtons.add(rowButtons);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(lineButtons);

        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup buildProfessorFunctionInlineKeyboard(Integer id) {
        List<List<InlineKeyboardButton>> lineButtons = new ArrayList<>();
        List<InlineKeyboardButton> rowButtons = new ArrayList<>();

        rowButtons.add(new InlineKeyboardButton().setText("Свернуть").setCallbackData("close"));
        lineButtons.add(rowButtons);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(lineButtons);

        return keyboardMarkup;
    }

    public static String computeAverageMark(Integer studentId) {
        List<BotUtils.Mark> marks = Server.getStudentMarks(studentId);
        double sum = 0.0;

        for (BotUtils.Mark mark : marks) {
            sum += mark.getValue();
        }

        BigDecimal res = new BigDecimal(marks.size() != 0 ? sum / marks.size() : 1.0);

        return res.setScale(1, RoundingMode.HALF_UP).toString();
    }
}
