package ninja.esgi.tvdbandroidapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

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

    public void connectionHandler(View view) {
        final Map<String, String> data = new HashMap<>();
        data.put("apikey", TdvbAPI.API_KEY);
        data.put("userkey", "hello");
        data.put("username", "world");
        final Spinner spinner = (Spinner) findViewById(R.id.planets_spinner);
        final LinearLayout layout = (LinearLayout) findViewById(R.id.main_login_layout);
        spinner.setVisibility(View.VISIBLE);
        layout.setVisibility(View.GONE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject json = TdvbAPI.sendPostRequest("login", data);
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
