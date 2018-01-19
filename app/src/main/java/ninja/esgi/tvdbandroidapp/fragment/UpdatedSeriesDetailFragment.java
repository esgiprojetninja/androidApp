package ninja.esgi.tvdbandroidapp.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.media.Rating;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import ninja.esgi.tvdbandroidapp.R;
import ninja.esgi.tvdbandroidapp.activity.UpdatedSeriesDetailActivity;
import ninja.esgi.tvdbandroidapp.activity.UpdatedSeriesListActivity;
import ninja.esgi.tvdbandroidapp.activity.dummy.DummyContent;
import ninja.esgi.tvdbandroidapp.model.UpdatedSerie;
import ninja.esgi.tvdbandroidapp.model.response.GetSerieDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.GetSerieResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserFavoritesResponse;
import ninja.esgi.tvdbandroidapp.networkops.ApiServiceManager;
import ninja.esgi.tvdbandroidapp.session.SessionStorage;
import retrofit2.Response;
import rx.Subscriber;

/**
 * A fragment representing a single UpdatedSeries detail screen.
 * This fragment is either contained in a {@link UpdatedSeriesListActivity}
 * in two-pane mode (on tablets) or a {@link UpdatedSeriesDetailActivity}
 * on handsets.
 */
public class UpdatedSeriesDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String LOG_TAG = "SeriesDetailFragment";


    private GetSerieDataResponse serieData;
    private SessionStorage session = null;
    private ApiServiceManager apiSm = null;
    private String tvShowID = null;
    private CollapsingToolbarLayout appBarLayout = null;
    Activity activity;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UpdatedSeriesDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = SessionStorage.getInstance(getContext());
        apiSm = new ApiServiceManager();
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            tvShowID = getArguments().getString(ARG_ITEM_ID);

            activity = this.getActivity();
            appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            adaptFavFABDisplay();
            fetchSeries();
        }
    }

    final private void adaptFavFABDisplay() {

        final FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.fab);
        if (session.isShowFavorite(tvShowID)) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_full_star));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteFavorite();
                }
            });
        } else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_border));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    putFavorite();
                }
            });
        }

    }

    private void loadData() {
        if (appBarLayout != null) {
            loadTitle();
            loadAverageRating();
            loadRatingDetails();
            loadStatus();
            loadGenres();
        }
    }

    final private void loadGenres() {
        TextView mText = (TextView) activity.findViewById(R.id.series_genres);
        mText.setText(serieData.getRatingsOverview(getResources()));
        if (serieData.getGenre() != null && serieData.getGenre().size() > 0)
            mText.setText(getResources().getString(R.string.series_genres_prefix) + " " + TextUtils.join(", ", serieData.getGenre()));
        else
            mText.setText(getString(R.string.series_no_genre));

    }

    final private void loadStatus() {
        if(serieData.getStatus() == null) return;
        Switch mSwitch = (Switch) activity.findViewById(R.id.switch1);

        switch (serieData.getStatus().toLowerCase()) {
            case "continuing": {
                mSwitch.setChecked(true);
                mSwitch.setText(getString(R.string.series_status_prefix) + " " + getString(R.string.series_status_continuing));
                break;
            }
            default: {
                mSwitch.setChecked(false);
                mSwitch.setText(getString(R.string.series_status_prefix) + " " + getString(R.string.series_status_ended));
                break;
            }
        }
    }

    private void loadAverageRating() {
        if (serieData.getSiteRating() == null) return;
        RatingBar mRating = (RatingBar) activity.findViewById(R.id.ratingBar);
        mRating.setRating(Float.parseFloat(String.valueOf(serieData.getSiteRating())));
    }

    final private void loadRatingDetails() {
        TextView mText = (TextView) activity.findViewById(R.id.rating_details);
        mText.setText(serieData.getRatingsOverview(getResources()));
    }

    private void loadTitle() {
        String name = serieData.getSeriesName();
        if ( name == null || name.length() <= 3)
            name = getString(R.string.unknown_tvshow_name);

        if (serieData.getFirstAired() != null && serieData.getFirstAired().contains("-"))
            name += " - " + serieData.getFirstAired().split("-")[0];
        appBarLayout.setTitle(name);
    }

    private void fetchSeries() {
        this.apiSm.getSerie(this.session.getSessionToken(), this.session.getUserLanguage(), Long.valueOf(tvShowID), new Subscriber<Response<GetSerieResponse>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_TAG, "error", e);
            }

            @Override
            public void onNext(Response<GetSerieResponse> response) {
                if (response.isSuccessful()) {
                    GetSerieResponse res = response.body();
                    serieData = res.getData();
                    loadData();
                    Log.d(LOG_TAG, "yeah mofo");
                } else {
                    Log.d(LOG_TAG, "uh oh, bad hat harry");
                }
            }
        });
    }

    final private void putFavorite() {
        this.apiSm.putUserFavorite(this.session.getSessionToken(), tvShowID, new Subscriber<Response<UserFavoritesResponse>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Response<UserFavoritesResponse> response) {
                if (response.isSuccessful()) {
                    UserFavoritesResponse userResponse = response.body();
                    session.setUserFavoritesShows(userResponse.getData().getFavorites());
                    adaptFavFABDisplay();
                    Snackbar.make(getView(), "Show added to your favorites", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                } else {
                    Log.d(LOG_TAG, "Failed to update user's favorites");
                }
            }
        });
    }

    final private void deleteFavorite() {
        this.apiSm.deleteUserFavorite(this.session.getSessionToken(), tvShowID, new Subscriber<Response<UserFavoritesResponse>>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Response<UserFavoritesResponse> response) {
                if (response.isSuccessful()) {
                    UserFavoritesResponse userResponse = response.body();
                    session.setUserFavoritesShows(userResponse.getData().getFavorites());
                    adaptFavFABDisplay();
                    Snackbar.make(getView(), "Show removed from your favorites", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                } else {
                    Log.d(LOG_TAG, "Failed to update user's favorites");
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.updatedseries_detail, container, false);

        return rootView;
    }
}
