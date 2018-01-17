package ninja.esgi.tvdbandroidapp.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.IOException;
import java.util.HashMap;
import ninja.esgi.tvdbandroidapp.R;
import ninja.esgi.tvdbandroidapp.model.Login;
import ninja.esgi.tvdbandroidapp.model.response.LoginResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserFavoritesResponse;
import ninja.esgi.tvdbandroidapp.networkops.ApiServiceManager;
import ninja.esgi.tvdbandroidapp.session.SessionStorage;
import retrofit2.Response;
import rx.Subscriber;

public class LoginActivity extends AppCompatActivity {
    private final String LOG_TAG = "LoginActivity";
    private SessionStorage session = null;
    private ApiServiceManager apiSm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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

    private void checkSession() {
        if (this.session.isUserConnected()) {
            this.goToMainActivity();
            return;
        }
    }

    private HashMap<String,String> controlFiels(View view) {
        EditText usernameDom = (EditText) view.findViewById(R.id.username_input);
        EditText userkeyDom = (EditText) view.findViewById(R.id.userkey_input);

        final String username = (String) usernameDom.getText().toString();
        final String userkey = (String) userkeyDom.getText().toString();

        if ( username != null && userkey != null && username.length() > 0 && userkey.length() > 0) {
            HashMap<String, String> map = new HashMap<>();
            map.put("username", username);
            map.put("userkey", userkey);
            return map;
        }
        return null;
    }

    final public void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void connectionHandler(View view) throws IOException {
        final HashMap<String, String> data = this.controlFiels(view.getRootView());
        if (data == null) {
            Log.d("INPUTS_ERROR", "control failed on username&||userkey");
            return;
        }
        final Login login = new Login(this.session.getApiKey(), data.get("username"), data.get("userkey"));
        this.dispatchLogin(login);
    }

    private void dispatchLogin(final Login login){
        this.showSpinner();
        this.apiSm.login(login, new Subscriber<Response<LoginResponse>>() {
            @Override
            public void onCompleted() {
                hideSpinner();
            }

            @Override
            public void onError(Throwable e) {
                hideSpinner();
            }

            @Override
            public void onNext(Response<LoginResponse> response) {
                hideSpinner();
                if (response.isSuccessful()) {
                    String token = response.body().getToken();
                    if (token != null && token.length() > 0) {
                        try {
                            session.setSessionToken(token);
                            session.setUserName(login.getUsername());
                            session.setUserKey(login.getUserkey());
                            fetchUserFavorites();
                            if (session.saveCredentials()) {
                                Log.d(LOG_TAG, "Saved credentials in session !");
                            } else {
                                Log.d(LOG_TAG, "FAILED to save credentials in session !");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        goToMainActivity();
                    }
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
                    session.setUserFavoritesShows(userResponse.getData().getFavorites());
                } else {
                    Log.d(LOG_TAG, "Failed to fetch user's favorites");
                }
            }
        });
    }
}
