package ninja.esgi.tvdbandroidapp;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

abstract class TdvbAPI {
    private static final String URI = "https://api.thetvdb.com/";
    private static final String API_KEY = "test";

    private TdvbAPI() {
    }

    private static JSONObject sendPostRequest(String method, Map<String, String> data) throws IOException, JSONException {
        // Create URL
        URL endpoint = new URL(String.format("%s%s", TdvbAPI.URI, method));
        // Create connection
        HttpsURLConnection connection = (HttpsURLConnection) endpoint.openConnection();
        connection.setRequestMethod("POST");

        // format data
        String urlParameters = "";
        for (Map.Entry<String, String> entry : data.entrySet())
        {
            urlParameters += entry.getKey() + "=" + entry.getValue() + "&";
        }
        urlParameters = urlParameters.substring(0, urlParameters.length() - 1);

        // Enable writing
        connection.setDoOutput(true);

        // Write the data
        connection.getOutputStream().write(urlParameters.getBytes());

        JSONObject json = new JSONObject();
        json.put("statusCode", connection.getResponseCode());
        if (connection.getResponseCode() == 200) {

            InputStream responseBody = connection.getInputStream();
            InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");
            JsonReader jsonReader = new JsonReader(responseBodyReader);
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {

                String key = jsonReader.nextName();
                String value = jsonReader.nextString();
                json.put(key, value);
            }
            jsonReader.close();
            if (!json.has("success")) {
                json.put("succes", true);
            }
        } else {
            json.put("succes", false);
        }
        connection.disconnect();
        Log.d("request response", json.toString());
        return json;
    }

    /**
     *
     * @APIparam {
        "apikey": "string",
        "userkey": "string",
        "username": "string"
        }
     * @APIreturn {"token": "string"}
     */
    public static Runnable login() {
        final Map<String, String> data = new HashMap<>();
        data.put("apikey", TdvbAPI.API_KEY);
        data.put("userkey", "hello");
        data.put("username", "world");
        return new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = TdvbAPI.sendPostRequest("login", data);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
