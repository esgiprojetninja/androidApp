package ninja.esgi.tvdbandroidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ninja.esgi.tvdbandroidapp.activity.LoginActivity;
import ninja.esgi.tvdbandroidapp.session.SharedStoragePrefs;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        this.adaptToolbar();
        this.adaptContent();
    }

    private void adaptToolbar() {
        Button loginBtn = (Button) findViewById(R.id.login_link);
        Button logoutBtn = (Button) findViewById(R.id.logout_link);
        TextView greetingsMsg = (TextView) findViewById(R.id.user_greetings);
        if (SharedStoragePrefs.isUserConnected()) {
            loginBtn.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.VISIBLE);
            greetingsMsg.setVisibility(View.VISIBLE);

            greetingsMsg.setText(SharedStoragePrefs.username);
        } else {
            loginBtn.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.GONE);
            greetingsMsg.setVisibility(View.GONE);
        }
    }

    private void adaptContent() {
        Button searchSeriesLink = (Button) findViewById(R.id.search_show_link_btn);
        if (SharedStoragePrefs.isUserConnected()) {
            searchSeriesLink.setVisibility(View.VISIBLE);
        } else {
            searchSeriesLink.setVisibility(View.GONE);
        }
    }

    public void loginActivity(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void logout(View view) {
        // @TODO clear creadentials, kill session, redirect to mainactivity
        Log.d("logout", "hovering out");
    }

    public void searchSeriesActivity(View view) {
        startActivity(new Intent(this, SearchSeries.class));
    }
}
