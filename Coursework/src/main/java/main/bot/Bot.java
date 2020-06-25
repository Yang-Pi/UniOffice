package main.bot;

import main.bot.features.BotFeatures;
import main.bot.utils.BotUtils;
import main.bot.utils.authentication.Authentication;
import main.bot.utils.commands.*;
import main.bot.utils.server.Server;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    //to identify different callbackQuery, after command execution it again will be "no_command"
    private static final String noCommandStatus = "no_command";
    private String _currentCommandName = noCommandStatus;
    private String _username = "";
    private String _userRole = "";

    //collect full password by symbols, after authentication it will be cleaned
    private String _password = "";
    private MessageInfo _lastMessageInfo = null; //to hide password keyboard
    private boolean _isSignin = false;

    private final MessageService messageService = new MessageService();

    private InlineKeyboardMarkup tmpMarkup = null;

    public Bot(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (_isSignin && !Server.checkToken()) {
            messageService.send(update, "Ваша сессия истекла, пожалуйста, авторизируйтесь повторно");
            return;
        }

        //Server.getGroups();
        if (update.hasMessage()) {
            String message = update.getMessage().getText();
            BotUtils.Response response = BotUtils.parseCommand(message);

            //if authentication was interrupt
            if (_lastMessageInfo != null && !_isSignin) {
                //messageService.editMarkup(_lastPasswordMessageInfo, null);
                messageService.editText(_lastMessageInfo, "Авторизация прервана");
                clearPasswordFields();
            }

            if (response.isValidCommand()) {
                if (!_isSignin && (!response.getCommandName().equals("start") && !response.getCommandName().equals("signin") && !response.getCommandName().equals("signup") && !response.getCommandName().equals("help"))) {
                    messageService.send(update, "Пожалуйста, выполните вход");
                    messageService.send(BotFeatures.setHelloDownKeybord(update.getMessage().getChatId()));
                    _currentCommandName = noCommandStatus;
                    return;
                }
                if (!_currentCommandName.equals(noCommandStatus)) {
                    _currentCommandName = noCommandStatus;
                }
                doCommand(update, response.getCommandName());
            }
            else {
                boolean isCommand = false;
                String text = update.getMessage().getText();
                String commandName = "";
                switch (message) {
                    case "Войти" : {
                        commandName = "signin";
                        isCommand = true;
                        break;
                    }
                    case "Зарегистрироваться" : {
                        commandName = "signup";
                        isCommand = true;
                        break;
                    }
                    case "Список групп" : {
                        commandName = "listgroups";
                        isCommand = true;
                        break;
                    }
                    case "Выйти" : {
                        commandName = "signout";
                        isCommand = true;
                        break;
                    }

                    case "Добавить студента или преподавателя" : {
                        commandName = "addperson";
                        isCommand = true;
                        break;
                    }
                    case "Добавить группу" : {
                        commandName = "addgroup";
                        isCommand = true;
                        break;
                    }
                    case "Добавить предмет" : {
                        commandName = "addsubject";
                        isCommand = true;
                        break;
                    }
                    case "О сервисе" : {
                        commandName = "info";
                        isCommand = true;
                        break;
                    }
                }

                if (isCommand) {
                    if (!_isSignin && (!commandName.equals("start") && !commandName.equals("signin") && !commandName.equals("signup") && !commandName.equals("info"))) {
                        messageService.send(update, "Пожалуйста, выполните вход");
                        messageService.send(BotFeatures.setHelloDownKeybord(update.getMessage().getChatId()));
                        _currentCommandName = noCommandStatus;
                        return;
                    }
                    else {
                        if (!_currentCommandName.equals(noCommandStatus)) {
                            _currentCommandName = noCommandStatus;
                        }
                        doCommand(update, commandName);
                    }
                }
                else if (!_currentCommandName.equals(noCommandStatus)) {
                    continueCommand(update, _currentCommandName);
                }
                else {
                    messageService.send(update, "Не болтаем лишнего!");
                }
            }
        }
        else if (update.hasCallbackQuery()) {
            if (_username.isEmpty()) {
                messageService.send(update, "Ваша сессия истекла, или вы не были авторизированы, " +
                        "для работы с сервисом сначала вызовете команду входа /signin");
                messageService.send(BotFeatures.setHelloDownKeybord(update.getCallbackQuery().getMessage().getChatId()));
                _currentCommandName = noCommandStatus;
                return;
            }
            doCallback(update);
        }
    }

    @Override
    public String getBotUsername() {
        return BotInfo.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return BotInfo.getBotToken();
    }

    private void doCommand(Update update, String commandName) {
        switch (commandName) {
            case "start" : {
                messageService.send(BotFeatures.setHelloDownKeybord(update.getMessage().getChatId()));
                break;
            }

            case "signin" : {
                if (!_isSignin) {
                    _currentCommandName = commandName;
                    _password = "";
                    String message = "Введите имя пользователя:";
                    messageService.send(update, message);
                }
                else {
                    String message = "Пользователь уже авторизирован. \n Для авторизация другого пользователя выйдите из текущей учетной записи";
                    messageService.send(update, message);
                }

                break;
            }

            case "signup" : {
                if (!_isSignin) {
                    _currentCommandName = commandName;
                    String message = "Регистриция нового пользователя";
                    messageService.send(update, message);
                    message = "Введите имя нового пользователя\n(латиницей и без пробелов):";
                    messageService.send(update, message);
                }
                else {
                    String message = "Вы сейчас авторизированы, для регистрации нового пользователя выйдите из своего аккаунта";
                    messageService.send(update, message);
                }


                break;
            }

            case "signout" : {
                String message = "Что ж ты, фраер, сдал назад...";
                messageService.editText(_lastMessageInfo, "...");
                messageService.send(update, message);
                clearPasswordFields();
                messageService.send(BotFeatures.setHelloDownKeybord(update.getMessage().getChatId()));

                break;
            }

            case "listgroups" : {
                //_currentCommandName = commandName;
                List<BotUtils.Group> groups = Server.getGroups();
                InlineKeyboardMarkup markup = ListGroups.buildGroupsInlineKeyboard(groups);
                String message = "Список групп";
                try {
                    if (update.hasMessage()) {
                        execute(BotFeatures.setInlineKeyboard(update.getMessage().getChatId(), message, markup));
                    }
                    else if (update.hasCallbackQuery()) {
                        Long chatId = update.getCallbackQuery().getMessage().getChatId();
                        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
                        messageService.editText(new MessageInfo(chatId, messageId), message);
                        messageService.editMarkup(new MessageInfo(chatId, messageId), markup);
                    }
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                break;
            }

            case "addperson" : {
                if (!_userRole.equals("STUDENT")) {
                    _currentCommandName = commandName;
                    String message = "Введите фамилию нового человека";
                    messageService.send(update, message);
                }
                else {
                    String message = "У вас нет прав доступа к этой функции";
                    messageService.send(update, message);
                }

                break;
            }

            case "addgroup" : {
                if (!_userRole.equals("STUDENT")) {
                    _currentCommandName = commandName;
                    String message = "Введите название новой группы в формате xxxxxxx.yyyyy";
                    InlineKeyboardMarkup markup = AddGroup.buildAddButton();
                    try {
                        Message responseMessage = execute(BotFeatures.setInlineKeyboard(update.getMessage().getChatId(), message, markup));
                        AddGroup.setMessageId(responseMessage.getMessageId());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    String message = "У вас нет прав доступа к этой функции";
                    messageService.send(update, message);
                }

                break;
            }

            case "addsubject" : {
                if (!_userRole.equals("STUDENT")) {
                    _currentCommandName = commandName;
                    String message = "Введите название предмета";
                    InlineKeyboardMarkup markup = AddSubject.buildAddButton();
                    try {
                        Message responseMessage = execute(BotFeatures.setInlineKeyboard(update.getMessage().getChatId(), message, markup));
                        AddSubject.setMessageId(responseMessage.getMessageId());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    String message = "У вас нет прав доступа к этой функции";
                    messageService.send(update, message);
                }

                break;
            }

            case "info" : {
                String message = "Бот UniOffice - вспомогательнй сервис для деканата ИКНТ Политеха. " +
                        "При возникновении технических неполадок обращаться к @pylaev";
                messageService.send(update, message);
            }
        }
    }

    private void continueCommand(Update update, String commandName) {
        switch (commandName) {
            case "signin" : {
                String message = "Наберите код-пароль, выданный Вам в деканате:";
                try {
                    InlineKeyboardMarkup markup = Authentication.buildInlineNumberKeyboard();
                    Message responseMessage = execute(BotFeatures.setInlineKeyboard(update.getMessage().getChatId(), message, markup));
                    _username = update.getMessage().getText();
                    _lastMessageInfo = new MessageInfo(responseMessage.getChatId(), responseMessage.getMessageId());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                break;
            }

            case "signup" : {
                if (_userRole.isEmpty()) {
                    _username = update.getMessage().getText();
                    if (Server.isUniqueUsername(_username)) {
                        String message = "Укажите статус пользователя";
                        try {
                            InlineKeyboardMarkup markup = Authentication.buildInlineStatusKeyboard();
                            execute(BotFeatures.setInlineKeyboard(update.getMessage().getChatId(), message, markup));
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        String message = "Такой пользователь уже существует, попробуйте ввести другой имя";
                        messageService.send(update, message);
                    }
                }
                else {
                    String message = "Задайте код-пароль:";
                    try {
                        InlineKeyboardMarkup markup = Authentication.buildInlineNumberKeyboard();
                        Message responseMessage = execute(BotFeatures.setInlineKeyboard(update.getCallbackQuery().getMessage().getChatId(), message, markup));
                        _lastMessageInfo = new MessageInfo(responseMessage.getChatId(), responseMessage.getMessageId());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }

                break;
            }

            case "addperson": {
                if (!AddPerson.wasFirstFieldSet()) {
                    AddPerson.setLastName(update.getMessage().getText());
                    AddPerson.setWasFirstFieldSet(true);
                    String message = "Введите его имя";
                    messageService.send(update, message);
                }
                else if (!AddPerson.wasSecondFieldSet()){
                    AddPerson.setFirstName(update.getMessage().getText());
                    AddPerson.setWasSecondFieldSet(true);
                    String message = "Теперь отчество";
                    messageService.send(update, message);
                }
                else {
                    AddPerson.setFatherName(update.getMessage().getText());
                    String message = "Осталось выбрать группу, куда будет зачислен новенький";
                    try {
                        List<BotUtils.Group> groups = Server.getGroups();
                        InlineKeyboardMarkup markup = ListGroups.buildGroupsInlineKeyboard(groups);

                        Message responseMessage = execute(BotFeatures.setInlineKeyboard(update.getMessage().getChatId(), message, markup));
                        AddPerson.setMessageIdGroups(responseMessage.getMessageId());
                        responseMessage = execute(BotFeatures.setInlineKeyboard(update.getMessage().getChatId(), "Добавляем?", AddPerson.buildInlineSpecKeyboard()));
                        AddPerson.setMessageIdToolsBar(responseMessage.getMessageId());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                break;
            }

            case "addgroup" : {
                String groupName = update.getMessage().getText();
                if (!Server.checkGroupName(groupName)) {
                    Integer pos = groupName.indexOf('/');
                    if (pos != -1) {
                        groupName = groupName.substring(0, pos) + "." + groupName.substring(pos + 1);
                    }
                    AddGroup.setName(groupName);
                }
                else {
                    messageService.send(update, "Такая группа уже существует");
                }

                break;
            }

            case "addsubject" : {
                String subjectName = update.getMessage().getText();
                if (!Server.checkSubjectName(subjectName)) {
                    AddSubject.setName(subjectName);
                }
                else {
                    messageService.send(update, "Предмет с таким названием уже существует");
                }

                break;
            }
        }
    }

    private void doCallback(Update update) {
        String commandName = "";

        if (!_currentCommandName.equals(noCommandStatus)) {
            commandName = _currentCommandName;
        }
        else if (update.getCallbackQuery().getData() != "") {
            int pos = update.getCallbackQuery().getData().indexOf('#');
            if (pos != -1) {
                commandName = update.getCallbackQuery().getData().substring(0, pos);
            }
            else {
                commandName = update.getCallbackQuery().getData();
            }
        }

        switch (commandName) {
            case "signin" : {
                doSigninCallBack(update);
                break;
            }
            case "signup" : {
                doSignupCallback(update);
                break;
            }
            case "signout" : {
                String message = "Что ж ты, фраер, сдал назад...";
                messageService.editText(_lastMessageInfo, message);
                messageService.send(BotFeatures.setHelloDownKeybord(update.getCallbackQuery().getMessage().getChatId()));
                clearPasswordFields();

                break;
            }

            case "backto" : {
                int pos = update.getCallbackQuery().getData().indexOf('#') + 1;
                commandName = update.getCallbackQuery().getData().substring(pos);
                doCommand(update, commandName);

                break;
            }

            case "addperson" : {
                doAddPersonCallback(update);
                break;
            }

            case "addgroup" : {
                String buttonInfo = update.getCallbackQuery().getData();
                String message = "Группа " + AddGroup.getName() + " успешно добавлена";

                if (buttonInfo.equals("addGroup_add")) {
                    if (!AddGroup.getName().isEmpty()) {
                        if (Server.addNewGroup(AddGroup.getName())) {
                            message = "Группа " + AddGroup.getName() + " успешно добавлена";
                        }
                        else {
                            message = "Что-то пошло не так";
                        }
                    }
                    else {
                        messageService.send(update, "Название группы не может быть пустым");
                        break;
                    }
                }
                else {
                    message = "Добавление группы прервано";
                }
                messageService.editText(new MessageInfo(update.getCallbackQuery().getMessage().getChatId(), AddGroup.getMessageId()), message);

                _currentCommandName = noCommandStatus;
                break;
            }

            case "addsubject" : {
                String buttonInfo = update.getCallbackQuery().getData();
                String message = "";

                if (buttonInfo.equals("addsubject_add")) {
                    if (!AddSubject.getName().isEmpty()) {
                        if (Server.addNewSubject(AddSubject.getName())) {
                            message = "Предмет " + AddSubject.getName() + " успешно добавлен";
                        }
                        else {
                            message = "Что-то пошло не так";
                        }
                    }
                    else {
                        messageService.send(update, "Название предмета не может быть пустым");
                        break;
                    }
                }
                else {
                    message = "Добавление предмета прервано";
                }
                messageService.editText(new MessageInfo(update.getCallbackQuery().getMessage().getChatId(), AddSubject.getMessageId()), message);

                _currentCommandName = noCommandStatus;
                break;
            }

            case "listgroups" : {
                Integer pos1 = update.getCallbackQuery().getData().indexOf('#') + 1;
                Integer pos2 = update.getCallbackQuery().getData().indexOf('%');
                Integer groupId = Integer.parseInt(update.getCallbackQuery().getData().substring(pos1, pos2));
                List<BotUtils.Person> people = Server.getPeople(groupId);

                InlineKeyboardMarkup markup = ListGroups.buildPeopleInlineKeyboard(people, groupId);
                String message = "Список студентов группы " + update.getCallbackQuery().getData().substring(pos2 + 1);
                Long chatId = update.getCallbackQuery().getMessage().getChatId();
                Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
                messageService.editText(new MessageInfo(chatId, messageId), message);
                messageService.editMarkup(new MessageInfo(chatId, messageId), markup);

                break;
            }

            case "person" : {
                Integer pos = update.getCallbackQuery().getData().indexOf('#') + 1;
                Integer personId = Integer.parseInt(update.getCallbackQuery().getData().substring(pos));

                BotUtils.Person person = Server.getPersonInfo(personId);
                String message = person.getLastName() + " " + person.getFirstName() + " " + person.getFatherName();
                InlineKeyboardMarkup markup = null;

                if (person.getType().equals("S")) {
                    markup = ListPeople.buildStudentFunctionInlineKeyboard(personId);
                }
                else {
                    markup = ListPeople.buildProfessorFunctionInlineKeyboard(personId);
                }
                try {
                    execute(BotFeatures.setInlineKeyboard(update.getCallbackQuery().getMessage().getChatId(), message, markup));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                break;
            }

            case "groupprogress" : {
                Integer pos = update.getCallbackQuery().getData().indexOf("#") + 1;
                Integer groupId = Integer.parseInt(update.getCallbackQuery().getData().substring(pos));

                List<BotUtils.Person> students = Server.getPeople(groupId);
                Double sum = 0.0;

                for (BotUtils.Person student : students) {
                    sum += Double.parseDouble(ListPeople.computeAverageMark(student.getId()));
                }

                Double res = sum / students.size();
                String message;
                if (res >= 4.0) {
                    message = "Средний балл: " + res.toString() + ", отличная группа!";
                }
                else if (res >= 3.0) {
                    message = "Средний балл: " + res.toString() + ", хорошая группа";
                }
                else {
                    message = "Средний балл: " + res.toString() + ", нужно взять на котроль";
                }

                try {
                    execute(new SendMessage(update.getCallbackQuery().getMessage().getChatId(), message));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                break;
            }

            case "close" : {
                String message = "Здесь был " + update.getCallbackQuery().getMessage().getText();
                MessageInfo messageInfo = new MessageInfo(update.getCallbackQuery().getMessage().getChatId(),
                        update.getCallbackQuery().getMessage().getMessageId());
                messageService.editText(messageInfo, message);

                break;
            }

            case "closeaddmark" : {
                String message = "Вы решили не ставить оценку";
                MessageInfo messageInfo = new MessageInfo(update.getCallbackQuery().getMessage().getChatId(),
                        update.getCallbackQuery().getMessage().getMessageId());
                messageService.editText(messageInfo, message);

                break;
            }

            case "personmarks" : {
                Integer pos = update.getCallbackQuery().getData().indexOf('#') + 1;
                Integer studentId = Integer.parseInt(update.getCallbackQuery().getData().substring(pos));
                List<BotUtils.Mark> marks = Server.getStudentMarks(studentId);

                String message = "";

                for (BotUtils.Mark mark : marks) {
                    message += mark.getSubject() + ": " + mark.getValue() + "\n";
                }

                if (message.isEmpty()) {
                    message = "Пока у студента нет оценок";
                }

                try {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
                    sendMessage.setText(message);
                    execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                break;
            }

            case "personaddmark" : {
                if (!_userRole.equals("PROFESSOR") && !_userRole.equals("MANAGER")) {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("У вас нет возможности выставлять оценки!");
                    sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    break;
                }

                Integer pos = update.getCallbackQuery().getData().indexOf("#") + 1;

                List<BotUtils.Subject> subjects = Server.getSubjects();
                List<BotUtils.Person> professors = Server.getPeople(Server.getProfessorsGroupId());
                InlineKeyboardMarkup markup = AddMark.buildSubjectsProfessorsInlineKeyboard(subjects, professors);
                String message = "Выберете предмет и преподавателя";
                try {
                    execute(BotFeatures.setInlineKeyboard(update.getCallbackQuery().getMessage().getChatId(), message, markup));
                    AddMark.setStudentId(Integer.parseInt(update.getCallbackQuery().getData().substring(pos)));
                    AddMark.setMarkup(markup);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                break;
            }

            case "addmarksubject" : {
                Integer pos1 = update.getCallbackQuery().getData().indexOf('#') + 1;
                Integer pos2 = update.getCallbackQuery().getData().indexOf('%') + 1;
                AddMark.setSubjectId(Integer.parseInt(update.getCallbackQuery().getData().substring(pos1, pos2 - 1)));
                AddMark.setSubjectName(update.getCallbackQuery().getData().substring(pos2));

                String message = AddMark.getSubjectName() + " " + AddMark.getProfessorName();
                MessageInfo messageInfo = new MessageInfo(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
                messageService.editText(messageInfo, message);
                messageService.editMarkup(messageInfo, AddMark.getMarkup());

                break;
            }

            case "addmarkprofessor" : {
                Integer pos1 = update.getCallbackQuery().getData().indexOf('#') + 1;
                Integer pos2 = update.getCallbackQuery().getData().indexOf('%') + 1;
                AddMark.setProfessorId(Integer.parseInt(update.getCallbackQuery().getData().substring(pos1, pos2 - 1)));
                AddMark.setProfessorName(update.getCallbackQuery().getData().substring(pos2));

                String message = AddMark.getSubjectName() + " " + AddMark.getProfessorName();
                MessageInfo messageInfo = new MessageInfo(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
                messageService.editText(messageInfo, message);
                messageService.editMarkup(messageInfo, AddMark.getMarkup());

                break;
            }

            case "addmarknext" : {
                InlineKeyboardMarkup markup = AddMark.buildMarksInlineKeyboard();
                String message = "Поставьте оценку";
                MessageInfo messageInfo = new MessageInfo(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
                messageService.editText(messageInfo, message);
                messageService.editMarkup(messageInfo, markup);

                break;
            }

            case "addmakback" : {
                InlineKeyboardMarkup markup = AddMark.getMarkup();
                String message = "Выберете предмет и преподавателя";
                try {
                    execute(BotFeatures.setInlineKeyboard(update.getCallbackQuery().getMessage().getChatId(), message, markup));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }

                break;
            }

            case "addmarkvalue" : {
                Integer pos = update.getCallbackQuery().getData().indexOf("#") + 1;
                AddMark.setMark(Integer.valueOf(update.getCallbackQuery().getData().substring(pos)));
                String message = "Будет поставлена " + AddMark.getMark();
                messageService.send(update, message);

                break;
            }

            case "addmark" : {
                boolean wasSet = Server.addMark(AddMark.getStudentId(), AddMark.getProfessorId(), AddMark.getSubjectId(), AddMark.getMark());
                String message = wasSet? "Оценка поставлена" : "Оценка не поставлена, что-то пошло не так";
                messageService.editText(new MessageInfo(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId()), message);

                break;
            }
        }
    }

    private void clearPasswordFields() {
        _username = "";
        _userRole = "";
        _password = "";
        _isSignin = false;
        _currentCommandName = noCommandStatus;
        _lastMessageInfo = null;
    }

    private void doSigninCallBack(Update update) {
        /*
        signin_password#1
        signin_ready
        signin_cancel
        */
        String callbackData = update.getCallbackQuery().getData();
        int pos = callbackData.indexOf("#");

        if (pos != -1 && callbackData.substring(0, pos).equals("sign_password")) {
            _password += callbackData.substring(pos + 1);
        }
        else if (callbackData.equals("sign_ready")) {
            try {
                List<InlineKeyboardButton> lineButtons = new ArrayList<>();
                InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                String message;

                String signinRes = Server.signInRequest(_username, _password);

                if (!signinRes.isEmpty()) {
                    _isSignin = true;
                    _userRole = Server.getRole(_username);

                    lineButtons.add(new InlineKeyboardButton().setText("Выйти из учетной записи").setCallbackData("signout"));
                    markup.setKeyboard(Collections.singletonList(lineButtons));
                    messageService.send(BotFeatures.setMainDownKeybord(update.getCallbackQuery().getMessage().getChatId()));

                    Server.setServerToken(signinRes);
                    Server.setUsername(_username);
                    Server.setPassword(_password);
                    _password = "";
                    message = "Авторизация прошла успешно";
                }
                else {
                    markup = null;
                    message = "Неверное имя пользователя или пароль";
                }

                messageService.editText(_lastMessageInfo, message);
                messageService.editMarkup(_lastMessageInfo, markup);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                _currentCommandName = noCommandStatus;
                if (!_isSignin) {
                    _lastMessageInfo = null;
                }
            }
        }
        else if (callbackData.equals("sign_cancel")) {
            messageService.editMarkup(_lastMessageInfo, null);
            messageService.editText(_lastMessageInfo, "Авторизация прервана");
            clearPasswordFields();
        }
    }

    private void doSignupCallback(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        int posPercent = callbackData.indexOf("%");
        int posHashtag = callbackData.indexOf("#");

        if (posPercent != -1 && callbackData.substring(0, posPercent).equals("sign_status")) {
            _userRole = callbackData.substring(posPercent + 1);

            String message = "Пользователь - ";
            if (_userRole.equals("STUDENT")) {
                message += "студент";
            }
            else if (_userRole.equals("PROFESSOR")) {
                message += "преподаватель";
            }
            else {
                message += "менеджер";
            }
            MessageInfo messageInfo = new MessageInfo(update.getCallbackQuery().getMessage().getChatId(), update.getCallbackQuery().getMessage().getMessageId());
            messageService.editText(messageInfo, message);
            messageService.editMarkup(messageInfo, Authentication.buildInlineStatusKeyboard());

            if (_lastMessageInfo == null) {
                continueCommand(update, _currentCommandName);
            }
        }
        else if (posHashtag != -1 && callbackData.substring(0, posHashtag).equals("sign_password")) {
            _password += callbackData.substring(posHashtag + 1);
        }
        else if (callbackData.equals("sign_ready")) {
            try {
                if (!_password.isEmpty()) {
                    List<InlineKeyboardButton> lineButtons = new ArrayList<>();
                    InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
                    String message;
                    boolean b = Server.signUpRequest(_username, _password, _userRole);
                    if (b) {
                        message = "Регистрация прошла успешно";
                    } else {
                        message = "Что-то пошло не так";
                    }
                    messageService.editText(_lastMessageInfo, message);
                } else {
                    messageService.send(update, "Нужно задать хоть одну цифру!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (!_isSignin) {
                    clearPasswordFields();
                }
            }
        }
        else if (callbackData.equals("sign_cancel")) {
            messageService.editMarkup(_lastMessageInfo, null);
            messageService.editText(_lastMessageInfo, "Авторизация прервана");
            clearPasswordFields();
        }
    }

    private void doAddPersonCallback(Update update) {
        String callbackData = update.getCallbackQuery().getData();
        int pos1 = callbackData.indexOf("#");
        int pos2 = callbackData.indexOf("%");

        if (pos2 != -1 && callbackData.substring(0, pos1).equals("listgroups")) {
            AddPerson.setGroupId(callbackData.substring(pos1 + 1, pos2));
            if (!callbackData.substring(pos2 + 1).equals("professors")) {
                AddPerson.setGroupType("S");
            }
            else {
                AddPerson.setGroupType("P");
            }
            //String message = "Новый человек будет добавлен в группу " + callbackData.substring(pos2 + 1);
            //messageService.send(update, message);
        }
        else if (callbackData.equals("addperson_add")) {
            if (AddPerson.getGroupType().isEmpty()) {
                return;
            }

            List<InlineKeyboardButton> lineButtons = new ArrayList<>();
            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            String message;

            boolean wasAdded = Server.addNewPerson(AddPerson.getFirstName(), AddPerson.getLastName(), AddPerson.getFatherName(), AddPerson.getGroupId(), AddPerson.getGroupType());

            if (wasAdded) {

                lineButtons.add(new InlineKeyboardButton().setText("Выйти из учетной записи").setCallbackData("signout"));
                markup.setKeyboard(Collections.singletonList(lineButtons));
                messageService.send(BotFeatures.setMainDownKeybord(update.getCallbackQuery().getMessage().getChatId()));

                message = AddPerson.getLastName() + " "  + AddPerson.getFirstName() + " " + AddPerson.getFatherName() + " успешно добавлен в группу";
            }
            else {
                markup = null;
                message = "Что-то пошло не так";
            }
            messageService.editText(new MessageInfo(update.getCallbackQuery().getMessage().getChatId(), AddPerson.getMessageIdGroups()), message);
            messageService.editText(new MessageInfo(update.getCallbackQuery().getMessage().getChatId(), AddPerson.getMessageIdToolsBar()), "Работаем дальше!");
            _currentCommandName = noCommandStatus;

            AddPerson.setWasFirstFieldSet(false);
            AddPerson.setWasSecondFieldSet(false);
        }
        else if (callbackData.equals("addperson_cancel")) {
            messageService.editText(new MessageInfo(update.getCallbackQuery().getMessage().getChatId(), AddPerson.getMessageIdGroups()), "Добавление прервано");
            messageService.editText(new MessageInfo(update.getCallbackQuery().getMessage().getChatId(), AddPerson.getMessageIdToolsBar()), "Можете попробовать еще раз или воспользоваться другими функциями");
            AddPerson.setWasFirstFieldSet(false);
            AddPerson.setWasSecondFieldSet(false);
        }
    }

    /*
    Special services for work with messages:
        - MessageService
            - sending
            - editing
        - MessageInfo
    */
    private class MessageService {
        synchronized void send(Update update, String message) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.enableMarkdown(true);
            if (update.hasMessage()) {
                sendMessage.setChatId(update.getMessage().getChatId().toString());
            }
            else if (update.hasCallbackQuery()) {
                sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
            }
            sendMessage.setText(message);
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        synchronized void send(SendMessage sendMessage) {
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        synchronized void editMarkup(MessageInfo messageInfo, InlineKeyboardMarkup markup) {
            EditMessageReplyMarkup editMarkup = new EditMessageReplyMarkup();

            editMarkup.setChatId(messageInfo.getChatId());
            editMarkup.setMessageId(messageInfo.getMessageId());
            editMarkup.setReplyMarkup(markup);

            try {
                execute(editMarkup);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        synchronized void editText(MessageInfo messageInfo, String text) {
            EditMessageText editText = new EditMessageText();

            editText.setChatId(messageInfo.getChatId());
            editText.setMessageId(messageInfo.getMessageId());
            editText.setText(text);

            try {
                execute(editText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private class MessageInfo {
        private final Long _chatId;
        private final Integer _messageId;

        public MessageInfo(Long chatId, Integer messageId) {
            _chatId = chatId;
            _messageId = messageId;
        }

        public Long getChatId() {
            return _chatId;
        }

        public Integer getMessageId() {
            return _messageId;
        }
    }
}
