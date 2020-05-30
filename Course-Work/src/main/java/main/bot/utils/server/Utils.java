package main.bot.utils.server;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class Utils {
    public static JSONObject makeJSON(HttpURLConnection http) throws JSONException {
        JSONObject res = null;
        try {
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                InputStream is = http.getInputStream();
                //pack response to JSON
                byte[] buffer = new byte[999];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                byte[] data = byteArrayOutputStream.toByteArray();
                String s = new String(data);

                JSONObject json = new JSONObject(s);
                res = json;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return res;
        }
    }

    public static JSONArray makeJSONArray(HttpURLConnection http) throws JSONException {
        JSONArray res = null;
        try {
            System.out.println(http.getResponseCode());
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                InputStream is = http.getInputStream();
                //pack response to JSON
                byte[] buffer = new byte[999];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }
                byte[] data = byteArrayOutputStream.toByteArray();
                String s = new String(data);

                System.out.println(s);

                JSONArray json = new JSONArray(s);
                res = json;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            return res;
        }
    }
}
