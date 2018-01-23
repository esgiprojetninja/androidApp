package ninja.esgi.tvdbandroidapp.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.AsyncTask;
import android.os.Build;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ninja.esgi.tvdbandroidapp.R;
import ninja.esgi.tvdbandroidapp.activity.UpdatedSeriesDetailActivity;
import ninja.esgi.tvdbandroidapp.activity.UpdatedSeriesListActivity;
import ninja.esgi.tvdbandroidapp.model.EpisodeDetail;
import ninja.esgi.tvdbandroidapp.model.response.GetSerieDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.GetSerieResponse;
import ninja.esgi.tvdbandroidapp.model.response.GetSeriesEpisodesResponse;
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
    Set<Map.Entry<Integer, List<EpisodeDetail>>> mappings = null;
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
            fetchSeriesEpisodes();
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
            loadOverview();
            if (serieData.getBanner() != null && serieData.getBanner().length() > 0 && serieData.getBanner().contains("graphical/")) {
                LoadImageFromURL loader = new LoadImageFromURL(activity.findViewById(R.id.toolbar_layout), serieData.getBanner());
                loader.execute();
            }
        }
    }

    final private void loadOverview() {
        TextView mText = (TextView) activity.findViewById(R.id.overview_text);
        if (serieData.getOverview() != null && serieData.getOverview().length() > 4 ) {
            mText.setText(serieData.getOverview());
        }  else {
            mText.setText(getResources().getString(R.string.series_no_overview));
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

    final private void loadAverageRating() {
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

    final private void loadTitle() {
        String name = serieData.getSeriesName();
        if ( name == null || name.length() <= 3)
            name = getString(R.string.unknown_tvshow_name);

        if (serieData.getFirstAired() != null && serieData.getFirstAired().contains("-"))
            name += " - " + serieData.getFirstAired().split("-")[0];
        appBarLayout.setTitle(name);
    }

    final private LinearLayout generateHZScrolledLayout() {
        LinearLayout sLayout = new LinearLayout(getContext());
        sLayout.setOrientation(LinearLayout.VERTICAL);
        sLayout.setClickable(true);
        sLayout.setMinimumWidth(600);
        sLayout.setMinimumHeight(300);
        sLayout.setPadding(5, 5,5, 5);
        ShapeDrawable rectShapeDrawable = new ShapeDrawable();
        Paint paint = rectShapeDrawable.getPaint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        sLayout.setBackgroundDrawable(rectShapeDrawable);
        return sLayout;
    }

    final private void loadSeasonEpisodes(List<EpisodeDetail> episodes) {
        LinearLayout mListContainer = (LinearLayout) activity.findViewById(R.id.episode_choice);
        mListContainer.removeAllViews();
        for(EpisodeDetail episode: episodes) {
            LinearLayout sLayout = generateHZScrolledLayout();

            TextView mTitle = new TextView(getContext());
            mTitle.setGravity(TextView.TEXT_ALIGNMENT_GRAVITY);
            String epNumber = getResources().getString(R.string.episode_ep_prefix);
            if (episode.getAiredEpisodeNumber() != null && episode.getAiredEpisodeNumber() >= 0) {
                epNumber += " " + episode.getAiredEpisodeNumber();
            } else {
                epNumber += " " + getResources().getString(R.string.episode_unknown_season);
            }
            mTitle.setText(epNumber);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mTitle.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            }
            mTitle.setTextColor(Color.BLACK);
            mTitle.setPadding(5, 5, 5, 10);

            TextView episodeName = new TextView(getContext());
            if (episode.getEpisodeName() != null && episode.getEpisodeName().length() > 2 && episode.getEpisodeName().compareTo( episode.getAiredEpisodeNumber().toString()) != 0) {
                episodeName.setText(episode.getEpisodeName());
            } else {
                episodeName.setText(getResources().getString(R.string.episode_unnamed));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                episodeName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            episodeName.setPadding(5, 15, 5, 10);


            sLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // @TODO load new episode activity
                }
            });

            sLayout.addView(mTitle);
            sLayout.addView(episodeName);
            mListContainer.addView(sLayout);
        }
    }

    final private void loadSeasonChoices() {
        LinearLayout mListContainer = (LinearLayout) activity.findViewById(R.id.season_choice);
        mListContainer.removeAllViews();

        for(final Map.Entry<Integer, List<EpisodeDetail>> mapping : mappings){
            LinearLayout sLayout = generateHZScrolledLayout();

            TextView mTitle = new TextView(getContext());
            mTitle.setGravity(TextView.TEXT_ALIGNMENT_GRAVITY);
            mTitle.setText("Season " +mapping.getKey());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mTitle.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            }
            mTitle.setTextColor(Color.BLACK);
            mTitle.setPadding(5, 5, 5, 10);



            TextView mEpNumber = new TextView(getContext());
            mEpNumber.setGravity(TextView.TEXT_ALIGNMENT_GRAVITY);
            if (mapping.getValue() != null && mapping.getValue().size() > 0 ) {
                mEpNumber.setText( mapping.getValue().size() + " episodes");
            } else {
                mEpNumber.setText( "No available episodes");
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mTitle.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
            }
            mEpNumber.setPadding(5, 15, 5, 10);

            sLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadSeasonEpisodes(mapping.getValue());
                }
            });

            sLayout.addView(mTitle);
            sLayout.addView(mEpNumber);
            mListContainer.addView(sLayout);
        }
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

    final private void fetchSeriesEpisodes() {
        this.apiSm.getSeriesEpisodes(this.session.getSessionToken(), this.session.getUserLanguage(), Long.valueOf(tvShowID), new Subscriber<Response<GetSeriesEpisodesResponse>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_TAG, "come one java", e);
            }

            @Override
            public void onNext(Response<GetSeriesEpisodesResponse> response) {
                if (response.isSuccessful()) {
                    GetSeriesEpisodesResponse episodes = response.body();
                    List<EpisodeDetail> episodesList = episodes.getData();
                    HashMap<String, List<EpisodeDetail>> episodesBySeasons = new HashMap<>();
                    for (EpisodeDetail episode: episodesList) {
                        Long season = episode.getAiredSeason();
                        String currentSeason = (season == null) ? "0" : season.toString();
                        if (episodesBySeasons.containsKey(currentSeason)) {
                            episodesBySeasons.get(currentSeason).add(episode);
                        } else {
                            List<EpisodeDetail> epsList = new ArrayList<EpisodeDetail>();
                            epsList.add(episode);
                            episodesBySeasons.put(currentSeason, epsList);
                        }
                    }
                    HashMap<Integer, List<EpisodeDetail>> episodesBySeasonsWithIntKeys = new HashMap<>();
                    for (HashMap.Entry<String, List<EpisodeDetail>> entry : episodesBySeasons.entrySet()) {
                        episodesBySeasonsWithIntKeys.put(Integer.valueOf(entry.getKey()), entry.getValue());
                    }
                    // Sorting by season
                    TreeMap<Integer, List<EpisodeDetail>> sorted = new TreeMap<>(episodesBySeasonsWithIntKeys);
                    mappings = sorted.entrySet();
                    // Sorting by episodes
                    for(Map.Entry<Integer, List<EpisodeDetail>> mapping : mappings){
                        Collections.sort(mapping.getValue());
                    }

                    loadSeasonChoices();
                } else {
                    Log.d(LOG_TAG, "Failed to fetch serie's episodes");
                }
            }
        });
    }

    public class LoadImageFromURL extends AsyncTask<String, Void, Bitmap> {
        private View viewToDrawOn;
        private String endUri;

        public LoadImageFromURL(View viewToDrawOn, String endUri) {
            this.viewToDrawOn = viewToDrawOn;
            this.endUri = endUri;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            // TODO Auto-generated method stub

            try {
                URL url = new URL("https://www.thetvdb.com/banners/"+this.endUri);
                InputStream is = url.openConnection().getInputStream();
                Bitmap bitMap = BitmapFactory.decodeStream(is);
                return bitMap;

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                viewToDrawOn.setBackground(new BitmapDrawable(activity.getResources(), result));
            }
        }

    }

}