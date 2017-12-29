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
import java.util.concurrent.FutureTask;

import javax.net.ssl.HttpsURLConnection;

abstract class TdvbAPI {
    private static final String URI = "https://api.thetvdb.com/";
    public static final String API_KEY = "test";

    private TdvbAPI() {
    }

    public static JSONObject sendPostRequest(String method, Map<String, String> data) throws IOException, JSONException, InterruptedException {
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
    public static void login(final FutureTask<JSONObject> future) {
    }
}
