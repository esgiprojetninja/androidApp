package ninja.esgi.tvdbandroidapp.activity;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.List;

import ninja.esgi.tvdbandroidapp.R;
import ninja.esgi.tvdbandroidapp.adapter.SearchedSerieAdapter;
import ninja.esgi.tvdbandroidapp.fragment.SearchSeriesDataDetailFragment;
import ninja.esgi.tvdbandroidapp.model.Search;
import ninja.esgi.tvdbandroidapp.model.response.LanguagesDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.LanguagesResponse;
import ninja.esgi.tvdbandroidapp.model.response.SearchSeriesDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.SearchSeriesResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserDetailResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserResponse;
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
    private ListView languagesListView;
    private ListView seriesResultListView;
    SearchedSerieAdapter searchedSeriesAdapter;
    private SearchSeriesDataDetailFragment newFragment = new SearchSeriesDataDetailFragment();

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

    final public void reloadSearchedSeriesList() {
        searchedSeriesAdapter.notifyDataSetChanged();
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
        if(this.session.areLanguagesLoaded()) {
            this.loadLanguages(this.session.getLanguages());
        } else {
            this.fetchLanguages();
        }
        if (!this.session.isUserInfoLoaded()) {
            this.fetchUserInfo();
        }
    }

    final private void loadLanguages(List<LanguagesDataResponse> languagesData) {
        final ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice);
        for(LanguagesDataResponse languageData: languagesData) {
            adapter.add(languageData.getName());
        }
        languagesListView = (ListView) findViewById(R.id.single_choice_list_view);
        languagesListView.setAdapter(adapter);
    }

    final public void clickCallback(SearchSeriesDataResponse tvShow) {
        newFragment.tvShow = tvShow;

        int position = languagesListView.getCheckedItemPosition();
        if(position > -1) {
            newFragment.language = session.getLanguages().get(position).getAbbreviation();
        } else {
            newFragment.language = session.getUserLanguage();
        }


        FragmentManager fragmentManager = getSupportFragmentManager();

        // The device is using a large layout, so show the fragment as a dialog
        newFragment.show(fragmentManager, "dialog");
    }

    final private void loadSearchedSeriesResponse(List<SearchSeriesDataResponse> seriesData) {
        LinearLayout lay = (LinearLayout) findViewById(R.id.search_series_result_container_layout);
        lay.setMinimumHeight(800);
        searchedSeriesAdapter  = new SearchedSerieAdapter(this, seriesData, this.session);

        seriesResultListView = (ListView) findViewById(R.id.search_result_list);
        seriesResultListView.setAdapter(searchedSeriesAdapter);
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

    final private void fetchUserInfo() {
        this.showSpinner();
        this.apiSm.getUser(this.session.getSessionToken(), new Subscriber<Response<UserResponse>>() {
            @Override
            public void onCompleted() {
                hideSpinner();
            }

            @Override
            public void onError(Throwable e) {
                hideSpinner();
            }

            @Override
            public void onNext(Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    UserResponse userResponse = response.body();
                    UserDetailResponse userDetailResponse = userResponse.getData();
                    session.setUserLanguage(userDetailResponse.getLanguage())
                            .setFavoriteDisplayMode(userDetailResponse.getFavoritesDisplaymode());
                } else {
                    Log.d(LOG_TAG, "uh oh, bad hat harry");
                }
            }
        });
    }

    final private void fetchSearchSeries(Search searchParams) {
        this.searchInProgress = true;
        this.showSpinner();
        this.apiSm.getSearchSeries(this.session.getSessionToken(), this.session.getUserLanguage(), searchParams, new Subscriber<Response<SearchSeriesResponse>>() {
            @Override
            public void onCompleted() {
                searchInProgress = false;
                hideSpinner();
            }

            @Override
            public void onError(Throwable e) {
                searchInProgress = false;
                Log.e(LOG_TAG, "error", e);
                hideSpinner();
            }

            @Override
            public void onNext(Response<SearchSeriesResponse> response) {
                if (response.isSuccessful()) {
                    SearchSeriesResponse res = response.body();
                    List<SearchSeriesDataResponse> resData = res.getData();
                    loadSearchedSeriesResponse(resData);
                } else {
                    Log.d(LOG_TAG, "uh oh, bad hat harry");
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

    private Search controlFiels() {
        Search searchParams = new Search();
        EditText seriesNameDom = (EditText) findViewById(R.id.search_series_name_input);
        EditText seriesImdbIdDom = (EditText) findViewById(R.id.search_series_imdb_id_input);
        EditText serieszap2itIdDom = (EditText) findViewById(R.id.search_series_zap2it_hint);

        int position = languagesListView.getCheckedItemPosition();
        if(position > -1) {
            LanguagesDataResponse language = session.getLanguages().get(position);
            searchParams.setLanguage(language.getAbbreviation());
        }

        final String seriesName = (String) seriesNameDom.getText().toString();
        if ( seriesName != null && seriesName.length() > 0) {
            searchParams.setName(seriesName);
        }

        final String seriesImdbId = (String) seriesImdbIdDom.getText().toString();
        if ( seriesImdbId != null && seriesImdbId.length() > 0) {
            searchParams.setImdbId(seriesImdbId);
        }

        final String serieszap2itId = (String) serieszap2itIdDom.getText().toString();
        if ( serieszap2itId != null && serieszap2itId.length() > 0) {
            searchParams.setZap2itId(serieszap2itId);
        }
        return searchParams.checkIsValid() ? searchParams : null;
    }

    public void triggerSearch(View view) {
        if (!this.searchInProgress) {
            Search searchParams = this.controlFiels();
            if (searchParams != null) {
                this.fetchSearchSeries(searchParams);
            } else {
                // @TODO display errors
            }
        }
    }

    public void handleFavorite(View view) {
        newFragment.handleFavorite();
    }
}
