package ninja.esgi.tvdbandroidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    public void loginActivity(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void logout(View view) {
        // @TODO clear creadentials, kill session, redirect to mainactivity
        Log.d("logout", "hovering out");
    }

}
