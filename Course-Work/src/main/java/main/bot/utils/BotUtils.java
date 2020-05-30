package main.bot.utils;

public class BotUtils {
    public static Response parseCommand(String sCommand) {
        Response response = new Response(false, "");

        if (!sCommand.isEmpty() && sCommand.substring(0, 1).equals("/")) {
            response = new Response(true, sCommand.substring(1, sCommand.length()));
        }

        return response;
    }

    public static class Response {
        private boolean _validCommand;
        private static String _commandName;

        Response(boolean status, String commandName) {
            _validCommand = status;
            _commandName = commandName;
        }

        public boolean isValidCommand() {
            return _validCommand;
        }

        public void setValidCommand(boolean status) {
            _validCommand = status;
        }

        public String getCommandName() {
            return _commandName;
        }

        public void setCommandName(String commandName) {
            _commandName = commandName;
        }
    }

    public static class Group {
        private Integer _id;
        private String _name;

        public Group(Integer _id, String _name) {
            this._id = _id;
            this._name = _name;
        }

        public Integer getId() {
            return _id;
        }

        public String getName() {
            return _name;
        }
    }

    public static class Person {
        private Integer _id;
        private String _firstName;
        private String _lastName;
        private String _fatherName;
        private String _type;

        public Person(Integer _id, String _firstName, String _lastName, String _fatherName, String _type) {
            this._id = _id;
            this._firstName = _firstName;
            this._lastName = _lastName;
            this._fatherName = _fatherName;
            this._type = _type;
        }

        public String getFirstName() {
            return _firstName;
        }

        public String getLastName() {
            return _lastName;
        }

        public String getFatherName() {
            return _fatherName;
        }

        public Integer getId() {
            return _id;
        }

        public String getType() {
            return _type;
        }
    }

    public static class Mark {
        private String _subject;
        private Integer _value;

        public Mark(String _subject, Integer _value) {
            this._subject = _subject;
            this._value = _value;
        }

        public String getSubject() {
            return _subject;
        }

        public Integer getValue() {
            return _value;
        }
    }

    public static class Subject {
        private Integer _id;
        private String _name;

        public Subject(Integer _id, String _name) {
            this._id = _id;
            this._name = _name;
        }

        public Integer getId() {
            return _id;
        }

        public String getName() {
            return _name;
        }
    }
}
