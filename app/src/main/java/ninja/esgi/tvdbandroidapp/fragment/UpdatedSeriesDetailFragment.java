package ninja.esgi.tvdbandroidapp.fragment;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import ninja.esgi.tvdbandroidapp.R;
import ninja.esgi.tvdbandroidapp.activity.UpdatedSeriesDetailActivity;
import ninja.esgi.tvdbandroidapp.activity.UpdatedSeriesListActivity;
import ninja.esgi.tvdbandroidapp.model.response.GetSerieDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.GetSerieResponse;
import ninja.esgi.tvdbandroidapp.model.response.SearchSeriesDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserFavoritesResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserRatingsDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserRatingsResponse;
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
    private boolean _userChangingRating = true;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public UpdatedSeriesDetailFragment() {
    }

    @Override
    public void onStop() {
        super.onStop();
        RatingBar mRating = (RatingBar) activity.findViewById(R.id.ratingBar);
        mRating.setOnRatingBarChangeListener(null);
    }

    final private void watchRatingBar() {
        RatingBar mRating = (RatingBar) activity.findViewById(R.id.ratingBar);
        mRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (_userChangingRating) {
                    if (v == 0)
                        deleteRating();
                    else {
                        int intV = (int) (v * 2);
                        putRating(String.valueOf(intV));
                    }
                }
                Log.d(LOG_TAG, "coucou");
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = SessionStorage.getInstance(getContext());
        apiSm = new ApiServiceManager();
        if (getArguments().containsKey(ARG_ITEM_ID)) {
            activity = this.getActivity();
            watchRatingBar();
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            tvShowID = getArguments().getString(ARG_ITEM_ID);


            appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            adaptFavFABDisplay();
            fetchUserRatings();
            fetchSeries();
            blockRatingBar(true);


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

    private void loadBasicData() {
        if (appBarLayout != null) {
            loadTitle();
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
        ImageView mImg = (ImageView) activity.findViewById(R.id.status_img);
        mImg.setBackgroundColor(Color.LTGRAY);

        Drawable sampleDrawable;

        TextView mText = (TextView) activity.findViewById(R.id.status_txt);

        switch (serieData.getStatus().toLowerCase()) {
            case "continuing": {
                sampleDrawable = getResources().getDrawable(R.drawable.ic_check_sign);
                mText.setText(getString(R.string.series_status_prefix) + " " + getString(R.string.series_status_continuing));
                break;
            }
            default: {
                sampleDrawable = getResources().getDrawable(R.drawable.ic_stop_sign);
                mText.setText(getString(R.string.series_status_prefix) + " " + getString(R.string.series_status_ended));
                break;
            }
        }
        mImg.setImageDrawable(sampleDrawable);
    }

    final private void blockRatingBar(boolean block) {
        RatingBar mRating = (RatingBar) activity.findViewById(R.id.ratingBar);
        mRating.setIsIndicator(block);
    }

    final private void loadRatingStars() {
        UserRatingsDataResponse userRating = session.getRatingIfExists(SearchSeriesDataResponse.ITEM_TYPE, tvShowID);
        if (userRating != null) {
            loadUserRatingStars(userRating);
        } else {
            loadAverageRating();
        }
    }

    final private void loadUserRatingStars(UserRatingsDataResponse userRating) {
        _userChangingRating = false;
        RatingBar mRating = (RatingBar) activity.findViewById(R.id.ratingBar);
        float rating = Float.parseFloat(String.valueOf(userRating.getRating())) / 2;
        mRating.setRating(rating);

        LayerDrawable stars = (LayerDrawable) mRating.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        _userChangingRating = true;

        Button ratingBtn = (Button) activity.findViewById(R.id.rating_cancel_btn);
        ratingBtn.setVisibility(View.VISIBLE);
        ratingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRating();
            }
        });
    }

    private void loadAverageRating() {
        _userChangingRating = false;
        if (serieData.getSiteRating() == null) return;
        RatingBar mRating = (RatingBar) activity.findViewById(R.id.ratingBar);
        float rating = Float.parseFloat(String.valueOf(serieData.getSiteRating())) / 2;
        mRating.setRating(rating);

        LayerDrawable stars = (LayerDrawable) mRating.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.DKGRAY, PorterDuff.Mode.SRC_ATOP);
        _userChangingRating = true;

        Button ratingBtn = (Button) activity.findViewById(R.id.rating_cancel_btn);
        ratingBtn.setVisibility(View.GONE);
        ratingBtn.setOnClickListener(null);
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
                    loadBasicData();
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

    final private void putRating(String rating) {
        blockRatingBar(true);
        this.apiSm.putUserRating(session.getSessionToken(), SearchSeriesDataResponse.ITEM_TYPE, tvShowID, rating, new Subscriber<Response<UserRatingsResponse>>() {
            @Override
            public void onCompleted() {
                blockRatingBar(false);
            }

            @Override
            public void onError(Throwable e) {
                blockRatingBar(false);
                Log.e(LOG_TAG, "putRating error", e);
            }

            @Override
            public void onNext(Response<UserRatingsResponse> res) {
                if (res.isSuccessful()) {
                    UserRatingsResponse ratingsResponse = res.body();
                    for (UserRatingsDataResponse userRating : ratingsResponse.getData()) {
                        if (userRating.getRatingType().compareTo(SearchSeriesDataResponse.ITEM_TYPE) == 0
                                && userRating.getRatingItemId().toString().compareTo(tvShowID) == 0) {
                            session.addUserRating(userRating);
                            loadUserRatingStars(userRating);
                            break;
                        }
                    }
                    fetchSeries();
                }
            }
        });
    }

    final private void deleteRating() {
        blockRatingBar(true);
        this.apiSm.deleteUserRating(session.getSessionToken(), SearchSeriesDataResponse.ITEM_TYPE, tvShowID, new Subscriber<Response<UserRatingsResponse>>() {
            @Override
            public void onCompleted() {
                blockRatingBar(false);
            }

            @Override
            public void onError(Throwable e) {
                blockRatingBar(false);
                Log.e(LOG_TAG, "putRating error", e);
            }

            @Override
            public void onNext(Response<UserRatingsResponse> res) {
                if (res.isSuccessful()) {
                    session.removeUserRating(SearchSeriesDataResponse.ITEM_TYPE, tvShowID);
                    loadAverageRating();
                }
            }
        });
    }

    private void fetchUserRatings() {
        blockRatingBar(true);
        this.apiSm.getUserRatings(this.session.getSessionToken(), new Subscriber<Response<UserRatingsResponse>>() {
            @Override
            public void onCompleted() {
                blockRatingBar(false); }

            @Override
            public void onError(Throwable e) {
                blockRatingBar(false); }

            @Override
            public void onNext(Response<UserRatingsResponse> response) {
                blockRatingBar(false);
                if (response.isSuccessful()) {
                    UserRatingsResponse userResponse = response.body();
                    session.setUserRatings(userResponse.getData());
                } else {
                    Log.d(LOG_TAG, "Failed to fetch user's favorites");
                }
                loadRatingStars();
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
