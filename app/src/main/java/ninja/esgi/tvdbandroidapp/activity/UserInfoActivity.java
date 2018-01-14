package ninja.esgi.tvdbandroidapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import ninja.esgi.tvdbandroidapp.R;
import ninja.esgi.tvdbandroidapp.model.response.UserDetailResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserFavoritesDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserFavoritesResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserRatingsDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserRatingsResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserResponse;
import ninja.esgi.tvdbandroidapp.networkops.ApiServiceManager;
import ninja.esgi.tvdbandroidapp.session.SessionStorage;
import retrofit2.Response;
import rx.Subscriber;

public class UserInfoActivity extends AppCompatActivity {
    private int _ongoingReqs = 0;
    private final String LOG_TAG = "UserInfoActivity";
    private SessionStorage session = null;
    private ApiServiceManager apiSm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        this.session = SessionStorage.getInstance(getApplicationContext());
        this.apiSm = new ApiServiceManager();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        this.checkSession();
        this.fetchUserFavorites();
        this.fetchUserRatings();
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

    private void checkSession() {
        if (!this.session.isUserConnected()) {
                startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        if (!this.session.isUserInfoLoaded()) {
            this.fetchUserInfo();
            return;
        }
        UserDetailResponse userDetailResponse = new UserDetailResponse();
        userDetailResponse.setUserName(this.session.getUserName());
        userDetailResponse.setLanguage(this.session.getUserLanguage());
        userDetailResponse.setFavoritesDisplaymode(this.session.getFavoritesDisplaymode());

        this.loadBasicData(userDetailResponse);
    }

    final private void loadBasicData(UserDetailResponse user) {
        TextView usernameDom = (TextView) findViewById(R.id.user_info_username);
        usernameDom.setText(user.getUserName());
        TextView userlanguageDom = (TextView) findViewById(R.id.user_info_language);
        userlanguageDom.setText(user.getLanguage());
        TextView userFavoriteDom = (TextView) findViewById(R.id.user_info_favorite_display_banners);
        userFavoriteDom.setText(user.getFavoritesDisplaymode());
    }

    final private void loadUserFavoritesData(UserFavoritesDataResponse user) {
        TextView favsMsg = (TextView) findViewById(R.id.user_favorites_msg_info);
        ListView listView = (ListView) findViewById(R.id.user_favorites_list);
        if (user.checkForDisplayableFavs()) {
            favsMsg.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice);
            for (String fav: user.getFavorites()) {
                adapter.add(fav);
            }
            listView.setAdapter(adapter);
        } else {
            favsMsg.setText(R.string.user_favorites_empty_msg);
        }
    }

    final void loadRatingsData(List<UserRatingsDataResponse> userRatingsData) {
        TextView ratsMsg = (TextView) findViewById(R.id.user_ratings_msg_info);
        ListView listView = (ListView) findViewById(R.id.user_ratings_list);

        if (userRatingsData.size() > 0) {
            ratsMsg.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice);
            for (UserRatingsDataResponse fav: userRatingsData) {
                adapter.add(fav.toString());
            }
            listView.setAdapter(adapter);
        } else {
            ratsMsg.setText(R.string.user_ratings_empty_msg);
        }
    }

    private void fetchUserInfo() {
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
                    loadBasicData(userResponse.getData());
                } else {
                    Log.d(LOG_TAG, "uh oh, bad hat harry");
                }
            }
        });
    }

    private void fetchUserFavorites() {
        this.showSpinner();
        this.apiSm.getUserFavorites(this.session.getSessionToken(), new Subscriber<Response<UserFavoritesResponse>>() {
            @Override
            public void onCompleted() {
                hideSpinner();
            }

            @Override
            public void onError(Throwable e) {
                hideSpinner();
            }

            @Override
            public void onNext(Response<UserFavoritesResponse> response) {
                if (response.isSuccessful()) {
                    UserFavoritesResponse userResponse = response.body();
                    loadUserFavoritesData(userResponse.getData());
                } else {
                    Log.d(LOG_TAG, "Failed to fetch user's favorites");
                }
            }
        });
    }

    private void fetchUserRatings() {
        this.showSpinner();
        this.apiSm.getUserRatings(this.session.getSessionToken(), new Subscriber<Response<UserRatingsResponse>>() {
            @Override
            public void onCompleted() { hideSpinner(); }

            @Override
            public void onError(Throwable e) { hideSpinner(); }

            @Override
            public void onNext(Response<UserRatingsResponse> response) {
                if (response.isSuccessful()) {
                    UserRatingsResponse userResponse = response.body();
                    loadRatingsData(userResponse.getData());
                } else {
                    Log.d(LOG_TAG, "Failed to fetch user's favorites");
                }
            }
        });
    }
}
