package ninja.esgi.tvdbandroidapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.HashMap;

import ninja.esgi.tvdbandroidapp.R;
import ninja.esgi.tvdbandroidapp.networkops.ApiServiceManager;
import ninja.esgi.tvdbandroidapp.session.SessionStorage;

public class SearchSeriesActivity extends AppCompatActivity {
    private final String LOG_TAG = "SearchSeriesActivity";
    private SessionStorage session = null;
    private ApiServiceManager apiSm = null;
    private boolean searchInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_series);

        this.session = SessionStorage.getInstance(getApplicationContext());
        this.apiSm = new ApiServiceManager();
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

    final private void showSpinner() {
        final Spinner popupSpinner = (Spinner) findViewById(R.id.login_spinner);
        if (popupSpinner.getVisibility() != View.VISIBLE) {
            popupSpinner.setVisibility(View.VISIBLE);
        }
    }

    final private void hideSpinner() {
        final Spinner popupSpinner = (Spinner) findViewById(R.id.login_spinner);
        if (popupSpinner.getVisibility() != View.GONE) {
            popupSpinner.setVisibility(View.GONE);
        }
    }

    private HashMap<String,String> controlFiels(View view) {
        EditText seriesNameDom = (EditText) view.findViewById(R.id.search_series_name_input);
        EditText userkeyDom = (EditText) view.findViewById(R.id.userkey_input);

        final String seriesName = (String) seriesNameDom.getText().toString();
        final String userkey = (String) userkeyDom.getText().toString();

        if ( seriesName != null && userkey != null && seriesName.length() > 0 && userkey.length() > 0) {
            HashMap<String, String> map = new HashMap<>();
            map.put("username", seriesName);
            map.put("userkey", userkey);
            return map;
        }
        return null;
    }

    public void triggerSearch(View view) {
        if (!this.searchInProgress) {
            // @TODO control text field
            // @TODO fetch on route
        }
    }
}
