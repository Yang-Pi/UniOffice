package tests.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UniOfficeApiTests {
    private static String _sMainUrl = "http://localhost:8080/api";
    private static String _token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzdSIsInJvbGVzIjpbIlNUVURFTlQiLCJQUk9GRVNTT1IiLCJNQU5BR0VSIl0sImlhdCI6MTU5MDkzMjgwNSwiZXhwIjoxNTkxMjMyODA1fQ.ir65tamlFIPx5QGZB7v6tcyGTkwVcnTn24SBjPj8GcM";
    ;

    @Test
    void testSignUp() {
        String username = "UnitTest";
        String password = "ThePassword";
        String role = "MANAGER";

        try {
            String sUrl = _sMainUrl + "/auth/signup";
            URL url = null;
            url = new URL(sUrl);
            HttpURLConnection http = null;
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

                assertEquals(HttpURLConnection.HTTP_OK, http.getResponseCode());

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                http.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testSignIn() {
        String username = "UnitTest";
        String password = "ThePassword";

        try {
            String sUrl = _sMainUrl + "/auth/signin";
            URL url = new URL(sUrl);
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                byte[] requestBody = ("{\"userName\":\"" + username + "\",\"password\":\"" + password + "\"}").getBytes(StandardCharsets.UTF_8);

                http.setFixedLengthStreamingMode(requestBody.length);
                http.connect();
                try (OutputStream os = http.getOutputStream()) {
                    os.write(requestBody);
                }

                assertEquals(HttpURLConnection.HTTP_OK, http.getResponseCode());

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                http.disconnect();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testGetSubjects() {
        String name = "Математические модели";
        assertAll(
                () -> assertEquals(HttpStatus.OK, getRequest("/subjects")),
                () -> assertEquals(HttpStatus.OK, getRequest("/checkSubjectName/" + name)),
                () -> assertEquals(HttpStatus.NOT_FOUND, getRequest("/checkSubjectName/" + "subject"))
        );
    }

    @Test
    void testGetGroups() {
        AtomicInteger id = new AtomicInteger(9);
        String name = "3530904.80001";

        assertAll(
                () -> assertEquals(HttpStatus.OK, getRequest("/groups")),
                () -> assertEquals(HttpStatus.OK, getRequest("/group/" + id)),
                () -> assertEquals(HttpStatus.NOT_FOUND, getRequest("/group/" + id.incrementAndGet())),
                () -> assertEquals(HttpStatus.OK, getRequest("/checkGroupName/" + name)),
                () -> assertEquals(HttpStatus.NOT_FOUND, getRequest("/checkGroupName/" + "group"))
        );
    }

    @Test
    void testPeople() {
        //number is id
        assertAll(
                () -> assertEquals(HttpStatus.OK, getRequest("/person/" + 10)),
                () -> assertEquals(HttpStatus.NOT_FOUND, getRequest("/person/" + 1000)),
                () -> assertEquals(HttpStatus.OK, getRequest("/student/" + 8 + "/marks")),
                () -> assertEquals(HttpStatus.NOT_FOUND, getRequest("/student/" + 1000 + "/marks"))
        );
    }

    @Test
    void testAdd() {
        String bodyAddMark = "{\"student\": {\"id\": " + 8 + "},\"subject\": {\"id\": " + 4
                + "},\"professor\": {\"id\": " + 10 + "}, \"value\": " + 5 + "}";

        String bodyAddPerson = "{\"firstName\": \"" + "Тест" + "\", \"lastName\": \"" + "Тестов"
                + "\",\"fatherName\": \"" + "Тестович" + "\", \"studentGroup\": {\"id\": " + 9
                + "}, \"type\": \"" + "S" + "\"}";

        String bodyAddSubject = "{\"name\": \"" + "ЭВМ" + "\"}";
        String bodyAddGroup = "{\"name\": \"" + "Группа UnitTest" + "\"}";

        assertAll(
                () -> assertNotNull(postRequest("/addMark", bodyAddMark)),
                () -> assertNotNull(postRequest("/addPerson", bodyAddPerson)),
                () -> assertNotNull(postRequest("/addSubject", bodyAddSubject)),
                () -> assertNotNull(postRequest("/addGroup", bodyAddGroup))
        );
    }

    public static HttpStatus getRequest(String endPartUrl) {
        HttpStatus res = HttpStatus.NOT_FOUND;
        try {
            String sUrl = _sMainUrl + endPartUrl;
            URL url = new URL(sUrl);
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("GET");
                http.setRequestProperty("Authorization", "Bearer "+ _token);
                http.setDoOutput(true);
                http.connect();

                res = HttpStatus.valueOf(http.getResponseCode());

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

    public static String postRequest(String endPartUrl, String json) {
        String res = null;
        try {
            String sUrl = _sMainUrl + endPartUrl;
            URL url = new URL(sUrl);
            HttpURLConnection http = null;
            try {
                http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setRequestProperty("Authorization", "Bearer "+ _token);
                http.setDoOutput(true);
                http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                byte[] requestBody = (json).getBytes(StandardCharsets.UTF_8);

                http.setFixedLengthStreamingMode(requestBody.length);
                http.connect();

                try(OutputStream os = http.getOutputStream()) {
                    os.write(requestBody);
                }

                if (http.getInputStream().available() != 0) {
                    res = http.getInputStream().toString();
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
