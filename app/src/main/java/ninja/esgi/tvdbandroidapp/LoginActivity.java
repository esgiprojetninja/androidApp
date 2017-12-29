package ninja.esgi.tvdbandroidapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private SharedStoragePrefs _storage = new SharedStoragePrefs();

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
            map.put("username", "bitechatte");
            map.put("userkey", userkey);
            return map;
        }
        return null;
    }

    public void connectionHandler(View view) {
        final Map<String, String> data = this.controlFiels(view.getRootView());
        if (data == null) {
            Log.d("INPUTS_ERROR", "control failed on username&||userkey");
            return;
        }
        data.put("apikey", BuildConfig.TDVB_API_KEY);
        final Spinner spinner = (Spinner) findViewById(R.id.planets_spinner);
        final LinearLayout layout = (LinearLayout) findViewById(R.id.main_login_layout);
        spinner.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = TdvbAPI.sendPostRequest("login", data);
                    if (json.has("status") && json.getBoolean("status") == true && json.has("token")) {
                        // save token in storage
                        _storage.saveToken(json.getString("token"));
                        // @TODO: save username in storage too
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    spinner.setVisibility(View.GONE);
                    layout.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
