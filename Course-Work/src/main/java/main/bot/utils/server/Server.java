package main.bot.utils.server;

import main.bot.utils.BotUtils;
import org.apache.http.HttpException;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static String _sMainUrl = "http://localhost:8080/api";
    private static String _serverToken;
    private static String _username;
    private static String _password;

    public static void setUsername(String username) {
        _username = username;
    }

    public static void setPassword(String password) {
        _password = password;
    }

    public static void setServerToken(String serverToken) {
        _serverToken = serverToken;
    }

    public static boolean checkToken() {
        boolean res = false;
        try {
            String sUrl = _sMainUrl + "/isValidToken/" + _serverToken;
            URL url = new URL(sUrl);
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("GET");
                http.setDoOutput(true);
                http.connect();

                if (http.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    signInRequest(_username, _password);
                    res = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                http.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        finally {
            return res;
        }
    }

    public static String signInRequest(final String username, final String password) throws IOException {
        String sUrl = _sMainUrl + "/auth/signin";
        URL url = new URL(sUrl);
        HttpURLConnection http = null;

        String res = "";

        try {
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            byte[] requestBody = ("{\"userName\":\"" + username + "\",\"password\":\"" + password + "\"}") .getBytes(StandardCharsets.UTF_8);

            http.setFixedLengthStreamingMode(requestBody.length);
            //http.setRequestProperty("Authorization", "Bearer "+ AuthController.getToken());
            http.connect();
            try(OutputStream os = http.getOutputStream()) {
                os.write(requestBody);
            }

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                res = (String) Utils.makeJSON(http).get("token");
            }
            else {
                throw new HttpException("Invalid login or password");
            }
        } catch (IOException | HttpException e) {
            e.printStackTrace();
        } finally {
            http.disconnect();
            return res;
        }
    }

    public static boolean signUpRequest(final String username, final String password, final String role) throws IOException {
        String sUrl = _sMainUrl + "/auth/signup";
        URL url = new URL(sUrl);
        HttpURLConnection http = null;

        boolean res = false;

        try {
            http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            byte[] requestBody = ("{\"userName\":\"" + username + "\",\"password\":\"" + password
                    + "\",\"role\":\"" + role + "\"}") .getBytes(StandardCharsets.UTF_8);

            http.setFixedLengthStreamingMode(requestBody.length);
            http.connect();
            try(OutputStream os = http.getOutputStream()) {
                os.write(requestBody);
            }

            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                res =  true;
            }
            else {
                throw new HttpException("Something wrong");
            }
        } catch (IOException | HttpException e) {
            e.printStackTrace();
        } finally {
            http.disconnect();
            return res;
        }
    }

    public static boolean isUniqueUsername(String username) {
        boolean res = false;
        try {
            String sUrl = _sMainUrl + "/auth/checkUsername/" + username;
            URL url = new URL(sUrl);
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("GET");
                http.setDoOutput(true);
                http.connect();

                if (http.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    res = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                http.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        finally {
            return res;
        }
    }

    public static String getRole(String username) {
        String res = "STUDENT";
        try {
            String sUrl = _sMainUrl + "/auth/checkUsername/" + username;
            URL url = new URL(sUrl);
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("GET");
                http.setDoOutput(true);
                http.connect();

                if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    JSONArray jsonArray = Utils.makeJSONArray(http);
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        res = String.valueOf(jsonArray.get(i));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                http.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        finally {
            return res;
        }
    }

    public static List<BotUtils.Group> getGroups() {
        List<BotUtils.Group> res = new ArrayList<>();
        try {
            String sUrl = _sMainUrl + "/groups";
            URL url = new URL(sUrl);
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("GET");
                http.setRequestProperty("Authorization", "Bearer "+ _serverToken);
                http.setDoOutput(true);
                http.connect();

                JSONArray jsonArray = Utils.makeJSONArray(http);

                for (int i = 0; i < jsonArray.length(); ++i) {
                    Integer id = Integer.parseInt(String.valueOf(jsonArray.getJSONObject(i).get("id")));
                    String name = (String) jsonArray.getJSONObject(i).get("name");
                    res.add(new BotUtils.Group(id, name));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                http.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        finally {
            return res;
        }
    }

    public static List<BotUtils.Person> getPeople(Integer groupId) {
        List<BotUtils.Person> res = new ArrayList<>();
        try {
            String sUrl = _sMainUrl + "/group/" + groupId;
            URL url = new URL(sUrl);
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("GET");
                http.setRequestProperty("Authorization", "Bearer "+ _serverToken);
                http.setDoOutput(true);
                http.connect();

                JSONArray jsonArray = Utils.makeJSONArray(http);

                for (int i = 0; i < jsonArray.length(); ++i) {
                    Integer id = Integer.parseInt(String.valueOf(jsonArray.getJSONObject(i).get("id")));
                    String firstName = (String) jsonArray.getJSONObject(i).get("firstName");
                    String lastName = (String) jsonArray.getJSONObject(i).get("lastName");
                    String fatherName = (String) jsonArray.getJSONObject(i).get("fatherName");
                    String type = (String) jsonArray.getJSONObject(i).get("type");
                    res.add(new BotUtils.Person(id, firstName, lastName, fatherName, type));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                http.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        finally {
            return res;
        }
    }

    public static BotUtils.Person getPersonInfo(Integer personId) {
        BotUtils.Person res = null;
        try {
            String sUrl = _sMainUrl + "/person/" + personId;
            URL url = new URL(sUrl);
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("GET");
                http.setRequestProperty("Authorization", "Bearer "+ _serverToken);
                http.setDoOutput(true);
                http.connect();

                JSONObject json = Utils.makeJSON(http);

                Integer id = Integer.parseInt(String.valueOf(json.get("id")));
                String firstName = (String) json.get("firstName");
                String lastName = (String) json.get("lastName");
                String fatherName = (String) json.get("fatherName");
                String  type = (String) json.get("type");
                res = new BotUtils.Person(id, firstName, lastName, fatherName, type);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                http.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        finally {
            return res;
        }
    }

    public static List<BotUtils.Mark> getStudentMarks(Integer studentId) {
        List<BotUtils.Mark> res = new ArrayList<>();
        System.out.println("ID: " + studentId);
        try {
            String sUrl = _sMainUrl + "/student/" + studentId + "/marks";
            URL url = new URL(sUrl);
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("GET");
                http.setRequestProperty("Authorization", "Bearer "+ _serverToken);
                http.setDoOutput(true);
                http.connect();

                JSONObject json = Utils.makeJSON(http);
                JSONArray jsonArray = json.getJSONArray("marks");

                for (int i = 0; i < jsonArray.length(); ++i) {
                    JSONObject subjectJSON = (JSONObject) jsonArray.getJSONObject(i).get("subject");
                    String subject = String.valueOf(subjectJSON.get("name"));
                    Integer value = Integer.parseInt(String.valueOf(jsonArray.getJSONObject(i).get("value")));
                    res.add(new BotUtils.Mark(subject, value));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                http.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        finally {
            return res;
        }
    }

    public static List<BotUtils.Subject> getSubjects() {
        List<BotUtils.Subject> res = new ArrayList<>();
        try {
            String sUrl = _sMainUrl + "/subjects";
            URL url = new URL(sUrl);
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("GET");
                http.setRequestProperty("Authorization", "Bearer "+ _serverToken);
                http.setDoOutput(true);
                http.connect();

                JSONArray jsonArray = Utils.makeJSONArray(http);

                for (int i = 0; i < jsonArray.length(); ++i) {
                    Integer id = Integer.parseInt(String.valueOf(jsonArray.getJSONObject(i).get("id")));
                    String name = (String) jsonArray.getJSONObject(i).get("name");
                    res.add(new BotUtils.Subject(id, name));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                http.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        finally {
            return res;
        }
    }

    public static boolean addMark(Integer studentId, Integer professorId, Integer subjectId, Integer value) {
        boolean res = false;
        try {
            String sUrl = _sMainUrl + "/addMark";
            URL url = new URL(sUrl);
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setRequestProperty("Authorization", "Bearer "+ _serverToken);
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                byte[] requestBody = ("{\"student\": {\"id\": " + studentId + "},\"subject\": {\"id\": " + subjectId
                        + "},\"professor\": {\"id\": " + professorId + "}, \"value\": " + value + "}").getBytes(StandardCharsets.UTF_8);

                http.setFixedLengthStreamingMode(requestBody.length);
                http.connect();

                try(OutputStream os = http.getOutputStream()) {
                    os.write(requestBody);
                }

                if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    res = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                http.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        finally {
            return res;
        }
    }

    public static boolean addNewPerson(String firstName, String lastName, String fatherName, String groupId, String type) {
        boolean res = false;
        try {
            String sUrl = _sMainUrl + "/addPerson";
            URL url = new URL(sUrl);
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setRequestProperty("Authorization", "Bearer "+ _serverToken);
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                byte[] requestBody = ("{\"firstName\": \"" + firstName + "\", \"lastName\": \"" + lastName
                        + "\",\"fatherName\": \"" + fatherName + "\", \"studentGroup\": {\"id\": " + groupId
                        + "}, \"type\": \"" + type + "\"}").getBytes(StandardCharsets.UTF_8);

                http.setFixedLengthStreamingMode(requestBody.length);
                http.connect();

                try(OutputStream os = http.getOutputStream()) {
                    os.write(requestBody);
                }

                if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    res = true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                http.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        finally {
            return res;
        }
    }
}
