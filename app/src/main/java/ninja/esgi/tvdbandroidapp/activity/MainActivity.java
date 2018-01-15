package ninja.esgi.tvdbandroidapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ninja.esgi.tvdbandroidapp.R;
import ninja.esgi.tvdbandroidapp.activity.LoginActivity;
import ninja.esgi.tvdbandroidapp.networkops.ApiServiceManager;
import ninja.esgi.tvdbandroidapp.session.SessionStorage;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = "MainActivity";
    private SessionStorage session = null;
    private ApiServiceManager apiSm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.session = SessionStorage.getInstance(getApplicationContext());
        this.apiSm = new ApiServiceManager();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        this.adaptToolbar();
        this.adaptContent();
    }

    private void adaptToolbar() {
        Button loginBtn = (Button) findViewById(R.id.login_link);
        Button logoutBtn = (Button) findViewById(R.id.logout_link);
        TextView greetingsMsg = (TextView) findViewById(R.id.user_greetings);
        if (this.session.isUserConnected()) {
            loginBtn.setVisibility(View.GONE);
            logoutBtn.setVisibility(View.VISIBLE);
            greetingsMsg.setVisibility(View.VISIBLE);

            greetingsMsg.setText(this.session.getUserName());
        } else {
            loginBtn.setVisibility(View.VISIBLE);
            logoutBtn.setVisibility(View.GONE);
            greetingsMsg.setVisibility(View.GONE);
        }
    }

    private void adaptContent() {
        Button userInfoLinkBtn = (Button) findViewById(R.id.user_info_link_btn);
        Button searchSeriesLinkBtn = (Button) findViewById(R.id.search_show_link_btn);
        if (this.session.isUserConnected()) {
            userInfoLinkBtn.setVisibility(View.VISIBLE);
            searchSeriesLinkBtn.setVisibility(View.VISIBLE);
        } else {
            userInfoLinkBtn.setVisibility(View.GONE);
            searchSeriesLinkBtn.setVisibility(View.GONE);
        }
    }

    public void loginActivity(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void logout(View view) {
        this.session.clearSession();
        Log.d(LOG_TAG, "hovering out");
        this.recreate();
    }

    public void userActivity(View view) {
        startActivity(new Intent(this, UserInfoActivity.class));
    }

    public void searchSeriesActivity(View view) {
        startActivity(new Intent(this, SearchSeriesActivity.class));
    }
}
