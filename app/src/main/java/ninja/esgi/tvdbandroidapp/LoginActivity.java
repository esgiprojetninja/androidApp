package ninja.esgi.tvdbandroidapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;


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
import okhttp3.ResponseBody;

public class LoginActivity extends AppCompatActivity {
    Spinner spinner;
    LinearLayout linear_layout;
    private SharedStoragePrefs _storage = new SharedStoragePrefs();
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
        spinner = (Spinner) findViewById(R.id.planets_spinner);
        linear_layout = (LinearLayout) findViewById(R.id.main_login_layout);
        spinner.setVisibility(View.VISIBLE);
        linear_layout.setVisibility(View.GONE);

        String urlBody = "{\n";
        for (Map.Entry<String, String> entry : data.entrySet())
        {
            urlBody += "\"" + entry.getKey() + "\": " + entry.getValue() + "\",";
        }
        urlBody = urlBody.substring(0, urlBody.length() - 1) + "}";
        this.postRequest(urlBody);
    }

    private void postRequest(String postBody) throws IOException {

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

                Log.d("TAG",response.body().string());
                ResponseBody resBody = response.body();
                String resString = resBody.string();
                // _storage.saveToken(json.getString("token"));
                // @TODO: save username in storage too
                spinner.setVisibility(View.GONE);
                linear_layout.setVisibility(View.VISIBLE);
            }
        });
    }
}
