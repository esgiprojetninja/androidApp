package ninja.esgi.tvdbandroidapp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import ninja.esgi.tvdbandroidapp.R;
import ninja.esgi.tvdbandroidapp.model.response.LanguagesDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.LanguagesResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserRatingsResponse;
import ninja.esgi.tvdbandroidapp.networkops.ApiServiceManager;
import ninja.esgi.tvdbandroidapp.session.SessionStorage;
import retrofit2.Response;
import rx.Subscriber;

public class SearchSeriesActivity extends AppCompatActivity {
    private final String LOG_TAG = "SearchSeriesActivity";
    private int _ongoingReqs = 0;
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

        this.checkSession();
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

    private void checkSession() {
        // @TODO check for session languages & user info before fetching
        if(this.session.areLanguagesLoaded()) {
            this.loadLanguages(this.session.getLanguages());
        } else {
            this.fetchLanguages();
        }
    }

    final private void loadLanguages(List<LanguagesDataResponse> languagesData) {
        final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice);
        for(LanguagesDataResponse languageData: languagesData) {
            adapter.add(languageData.getName());
        }
        final ListView listView = (ListView) findViewById(R.id.single_choice_list_view);
        listView.setAdapter(adapter);

        Button button = (Button) findViewById(R.id.bottom_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = listView.getCheckedItemPosition();
                if(position > -1) {
                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) listView.getAdapter();
                    Toast.makeText(SearchSeriesActivity.this, adapter.getItem(position), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    final private void fetchLanguages() {
        this.showSpinner();
        this.apiSm.getLanguages(this.session.getSessionToken(), new Subscriber<Response<LanguagesResponse>>() {
            @Override
            public void onCompleted() { hideSpinner(); }

            @Override
            public void onError(Throwable e) { hideSpinner(); }

            @Override
            public void onNext(Response<LanguagesResponse> response) {
                if (response.isSuccessful()) {
                    LanguagesResponse languageResponse = response.body();
                    List<LanguagesDataResponse> languagesData = languageResponse.getData();
                    session.setLanguages(languagesData);
                    loadLanguages(languagesData);
                } else {
                    Log.d(LOG_TAG, "Failed to fetch user's favorites");
                }
            }
        });
    }

    final private void showSpinner() {
        this._ongoingReqs += 1;
        final Spinner popupSpinner = (Spinner) findViewById(R.id.login_spinner);
        if (popupSpinner.getVisibility() != View.VISIBLE) {
            popupSpinner.setVisibility(View.VISIBLE);
        }
    }

    final private void hideSpinner() {
        final Spinner popupSpinner = (Spinner) findViewById(R.id.login_spinner);
        this._ongoingReqs -= 1;
        if (popupSpinner.getVisibility() != View.GONE && this._ongoingReqs == 0) {
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
