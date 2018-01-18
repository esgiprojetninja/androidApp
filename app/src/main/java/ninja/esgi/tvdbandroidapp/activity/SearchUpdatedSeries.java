package ninja.esgi.tvdbandroidapp.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ninja.esgi.tvdbandroidapp.R;
import ninja.esgi.tvdbandroidapp.model.response.UpdatedSeriesResponse;
import ninja.esgi.tvdbandroidapp.networkops.ApiServiceManager;
import ninja.esgi.tvdbandroidapp.session.SessionStorage;
import retrofit2.Response;
import rx.Subscriber;


public class SearchUpdatedSeries extends AppCompatActivity {
    private static String LOG_TAG = "SearchUpdatedSeries";
    private static int ONE_DAY = 60*60*24*1*1000;
    private static int SEVEN_DAYS = 60*60*24*7*1000;
    final private Calendar myStartDate = Calendar.getInstance();
    final private Calendar myEndDate = Calendar.getInstance();
    private SessionStorage session = null;
    private ApiServiceManager apiSm = null;

    // UI references.
    private EditText mStartDateView;
    private EditText mEndDateView;
    private View mProgressView;
    private View mSearchFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updated_series);

        session = SessionStorage.getInstance(getApplicationContext());
        apiSm = new ApiServiceManager();

        loadToolbar();

        myStartDate.add(Calendar.DAY_OF_MONTH, -7);
        adaptTextFieldsToDates();

        // Set up form
        mStartDateView = (EditText) findViewById(R.id.from_time);
        mStartDateView.setKeyListener(null);

        mEndDateView = (EditText) findViewById(R.id.prompt_to_time);
        mEndDateView.setKeyListener(null);

        Button mSearchButton = (Button) findViewById(R.id.search_updated_series_btn);
        mSearchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchSearchSeries();
            }
        });

        mSearchFormView = findViewById(R.id.updated_series_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private final void loadToolbar() {
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void adaptTextFieldsToDates() {
        String myFormat = "MM/dd/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRENCH);

        EditText mFromDate = findViewById(R.id.from_time);
        mFromDate.setText(sdf.format(myStartDate.getTime()));

        EditText mToDate = findViewById(R.id.prompt_to_time);
        mToDate.setText(sdf.format(myEndDate.getTime()));
    }


    final private void fetchSearchSeries() {
        showProgress(true);
        final String fromTime = Long.toString( myStartDate.getTime().getTime() / 1000);
        final String toTime = Long.toString( myEndDate.getTime().getTime() / 1000);
        this.apiSm.getUpdatedSeries(this.session.getSessionToken(), this.session.getUserLanguage(), fromTime, toTime, new Subscriber<Response<UpdatedSeriesResponse>>() {
            @Override
            public void onCompleted() {
                showProgress(false);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_TAG, "error", e);
                showProgress(false);
            }

            @Override
            public void onNext(Response<UpdatedSeriesResponse> response) {
                if (response.isSuccessful()) {
                    UpdatedSeriesResponse res = response.body();
                    Log.d(LOG_TAG, fromTime);
                    Log.d(LOG_TAG, toTime);
                } else {
                    Log.d(LOG_TAG, "uh oh, bad hat harry");
                }
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mSearchFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mSearchFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mSearchFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mSearchFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void datePicker(View view) {
        final boolean isStartingDate = getResources().getResourceName(view.getId()).contains("from_time");
        final Calendar receivedDate = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                receivedDate.set(Calendar.YEAR, year);
                receivedDate.set(Calendar.MONTH, monthOfYear);
                receivedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Date date = receivedDate.getTime();
                if (isStartingDate) {
                    if (date.getTime() > myEndDate.getTime().getTime()) {
                        Date newEndDate = new Date(date.getTime() + ONE_DAY);
                        myEndDate.set(Calendar.YEAR, newEndDate.getYear() + 1900);
                        myEndDate.set(Calendar.MONTH, newEndDate.getMonth());
                        myEndDate.set(Calendar.DAY_OF_MONTH, newEndDate.getDay());
                    }
                    myStartDate.set(Calendar.YEAR, date.getYear());
                    myStartDate.set(Calendar.MONTH, date.getMonth());
                    myStartDate.set(Calendar.DAY_OF_MONTH, date.getDay());

                } else {
                    if (date.getTime() < myStartDate.getTime().getTime()) {
                        Date newEndDate = new Date(date.getTime() - ONE_DAY);
                        myStartDate.set(Calendar.YEAR, newEndDate.getYear() + 1900);
                        myStartDate.set(Calendar.MONTH, newEndDate.getMonth());
                        myStartDate.set(Calendar.DAY_OF_MONTH, newEndDate.getDay());
                    }
                    myEndDate.set(Calendar.YEAR, year);
                    myEndDate.set(Calendar.MONTH, monthOfYear);
                    myEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                }
                adaptTextFieldsToDates();
            }

        };
        final Calendar targetDate = (isStartingDate) ? myStartDate : myEndDate;
        DatePickerDialog dialog = new DatePickerDialog(this, date, targetDate.get(Calendar.YEAR), targetDate.get(Calendar.MONTH), targetDate.get(Calendar.DAY_OF_MONTH));
        if (isStartingDate)
            dialog.getDatePicker().setMaxDate(new Date().getTime() - ONE_DAY);
        else
            dialog.getDatePicker().setMaxDate(new Date().getTime());
        dialog.show();
    }
}

