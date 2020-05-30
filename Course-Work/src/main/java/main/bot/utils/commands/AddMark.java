package main.bot.utils.commands;

import main.bot.utils.BotUtils;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.max;

public class AddMark {
    private static Integer _studentId = 0;

    private static InlineKeyboardMarkup _markup = null;
    private static Integer _subjectId = 0;
    private static Integer _professorId = 0;
    private static String _subjectName = "";
    private static String _professorName = "";
    private static Integer _mark;

    public static Integer getStudentId() {
        return _studentId;
    }

    public static void setStudentId(Integer _studentId) {
        AddMark._studentId = _studentId;
    }

    public static String getSubjectName() {
        return _subjectName;
    }

    public static void setSubjectName(String _subjectName) {
        AddMark._subjectName = _subjectName;
    }

    public static String getProfessorName() {
        return _professorName;
    }

    public static void setProfessorName(String _professorName) {
        AddMark._professorName = _professorName;
    }

    public static InlineKeyboardMarkup getMarkup() {
        return _markup;
    }

    public static void setMarkup(InlineKeyboardMarkup _markup) {
        AddMark._markup = _markup;
    }

    public static Integer getSubjectId() {
        return _subjectId;
    }

    public static void setSubjectId(Integer _subjectId) {
        AddMark._subjectId = _subjectId;
    }

    public static Integer getProfessorId() {
        return _professorId;
    }

    public static void setProfessorId(Integer _professorId) {
        AddMark._professorId = _professorId;
    }

    public static Integer getMark() {
        return _mark;
    }

    public static void setMark(Integer _mark) {
        AddMark._mark = _mark;
    }

    public static InlineKeyboardMarkup buildSubjectsProfessorsInlineKeyboard
            (List<BotUtils.Subject> subjects, List<BotUtils.Person> professors) {
        List<List<InlineKeyboardButton>> lineButtons = new ArrayList<>();
        List<InlineKeyboardButton> rowButtons = new ArrayList<>();

        rowButtons.add(new InlineKeyboardButton().setText("Свернуть").setCallbackData("closeaddmark"));
        lineButtons.add(rowButtons);
        rowButtons = new ArrayList<>();

        for (int i = 0; i < (max(subjects.size(), professors.size())); ++i) {
            if (i < subjects.size()) {
                System.out.println(subjects.get(i).getId() + " " + subjects.get(i).getName());
                rowButtons.add(new InlineKeyboardButton().setText(subjects.get(i).getName())
                        .setCallbackData("addmarksubject#" + subjects.get(i).getId() + "%" + subjects.get(i).getName()));
            }
            if (i < professors.size()) {
                String name = professors.get(i).getLastName() + " " + professors.get(i).getFirstName() + " " + professors.get(i).getFatherName();
                rowButtons.add(new InlineKeyboardButton().setText(name)
                        .setCallbackData("addmarkprofessor#" + professors.get(i).getId() + "%" + professors.get(i).getLastName()));
            }
            lineButtons.add(rowButtons);
            rowButtons = new ArrayList<>();
        }

        rowButtons.add(new InlineKeyboardButton().setText("Далее").setCallbackData("addmarknext"));
        lineButtons.add(rowButtons);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(lineButtons);

        return keyboardMarkup;
    }

    public static InlineKeyboardMarkup buildMarksInlineKeyboard() {
        List<List<InlineKeyboardButton>> lineButtons = new ArrayList<>();
        List<InlineKeyboardButton> rowButtons = new ArrayList<>();

        rowButtons.add(new InlineKeyboardButton().setText("Назад").setCallbackData("addmarkback"));
        lineButtons.add(rowButtons);
        rowButtons = new ArrayList<>();

        for (int i = 0; i < 5; ++i) {
            rowButtons.add(new InlineKeyboardButton().setText(String.valueOf(i + 1))
                    .setCallbackData("addmarkvalue#" + String.valueOf(i + 1)));
            if (i + 1 == 3 || i + 1 == 5) {
                lineButtons.add(rowButtons);
                rowButtons = new ArrayList<>();
            }
        }

        rowButtons.add(new InlineKeyboardButton().setText("Поставить").setCallbackData("addmark"));
        lineButtons.add(rowButtons);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(lineButtons);

        return keyboardMarkup;
    }
}
