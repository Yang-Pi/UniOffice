package tests.api;

import org.json.JSONException;
import org.json.JSONObject;

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
}
