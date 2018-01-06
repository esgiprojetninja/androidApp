package ninja.esgi.tvdbandroidapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "tdvb.android.app";
    public final static String TOKEN_KEY = String.format("%s.tdvb_user_token", PREFS_NAME);
    private final String _postUrl = "https://api.thetvdb.com/login";
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private Map<String,String> controlFiels(View view) {
        EditText usernameDom = (EditText) view.findViewById(R.id.username_input);
        EditText userkeyDom = (EditText) view.findViewById(R.id.userkey_input);

        final String username = (String) usernameDom.getText().toString();
        final String userkey = (String) userkeyDom.getText().toString();

        if ( username != null && userkey != null && username.length() > 0 && userkey.length() > 0) {
            Map<String, String> map = new HashMap<>();
            map.put("username", username);
            map.put("userkey", userkey);
            return map;
        }
        return null;
    }

    public void connectionHandler(View view) throws IOException {
        final Map<String, String> data = this.controlFiels(view.getRootView());
        if (data == null) {
            Log.d("INPUTS_ERROR", "control failed on username&||userkey");
            return;
        }
        data.put("apikey", BuildConfig.TDVB_API_KEY);
        String urlBody = "{\n";
        for (Map.Entry<String, String> entry : data.entrySet())
        {
            urlBody += "\"" + entry.getKey() + "\": \"" + entry.getValue() + "\",";
        }
        urlBody = urlBody.substring(0, urlBody.length() - 1) + "}";
        this.postRequest(urlBody, data);
    }

    final private void toggleSpinner() {
        final Spinner popupSpinner = (Spinner) findViewById(R.id.login_spinner);
        if (popupSpinner.getVisibility() == View.VISIBLE) {
            popupSpinner.setVisibility(View.GONE);
        } else {
            popupSpinner.setVisibility(View.VISIBLE);
        }
    }

    private void postRequest(String postBody, final Map<String, String> bodyData) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, postBody);

        Request request = new Request.Builder()
                .url(_postUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    if (response.code() == 200) {
                        String resString = response.body().string();
                        JSONObject json = new JSONObject(resString);

                        if (json.has("token")) {
                            String token = json.getString("token");
                            final Context context = getApplicationContext();
                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(SharedStoragePrefs.TOKEN_KEY, token);
                            editor.putString(SharedStoragePrefs.USER_KEY, bodyData.get("userkey"));
                            editor.putString(SharedStoragePrefs.USER_NAME, bodyData.get("username"));
                            if( editor.commit() ) {
                                Log.d("successTokenStorage", token);
                            }
                        }
                    } else {
                        // @TODO : Display auth fail msg
                        Log.d("fuuuuck",  response.body().string());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
