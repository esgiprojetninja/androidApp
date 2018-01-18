package ninja.esgi.tvdbandroidapp.fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import ninja.esgi.tvdbandroidapp.R;
import ninja.esgi.tvdbandroidapp.activity.SearchSeriesActivity;
import ninja.esgi.tvdbandroidapp.model.EpisodeDetail;
import ninja.esgi.tvdbandroidapp.model.response.GetSerieDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.GetSerieResponse;
import ninja.esgi.tvdbandroidapp.model.response.GetSeriesEpisodesResponse;
import ninja.esgi.tvdbandroidapp.model.response.SearchSeriesDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserFavoritesResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserRatingsDataResponse;
import ninja.esgi.tvdbandroidapp.networkops.ApiServiceManager;
import ninja.esgi.tvdbandroidapp.session.SessionStorage;
import retrofit2.Response;
import rx.Subscriber;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class SearchSeriesDataDetailFragment extends DialogFragment {
    final private static String LOG_TAG = "SearchSeriesDDFragment";
    private int _neeededResponses = 2;
    public SearchSeriesDataResponse tvShow;
    public String language;
    private int _ongoingReqs = 0;
    private SessionStorage session = null;
    private ApiServiceManager apiSm = null;
    private GetSerieDataResponse tvShowDetails = null;
    View view;

    public SearchSeriesDataDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_search_series_data_detail, container, true);
        this.session = SessionStorage.getInstance(getContext());
        this.apiSm = new ApiServiceManager();
        this._neeededResponses = 2;
        this.fetchData();
        this.adaptRatingsDisplay();

        // @TODO GET series/{id}/actors
        // @TODO GET episodes/{id} on click ?
        // @TODO GET /user/ratings
        // @TODO PUT /user/ratings/{itemType}/{itemId}/{itemRating}
        // @TODO DELETE /user/ratings/{itemType}/{itemId}
        return view;
    }

    @Override
    public void onStop() {
        SearchSeriesActivity activity = (SearchSeriesActivity) getActivity();
        activity.reloadSearchedSeriesList();
        super.onStop();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    final private void showSpinner() {
        _ongoingReqs += 1;
        final Spinner popupSpinner = (Spinner) view.findViewById(R.id.login_spinner);
        if (popupSpinner.getVisibility() != View.VISIBLE) {
            popupSpinner.setVisibility(View.VISIBLE);
        }
    }

    final private void hideSpinner() {
        final Spinner popupSpinner = (Spinner) view.findViewById(R.id.login_spinner);
        _ongoingReqs -= 1;
        if (popupSpinner.getVisibility() != View.GONE && _ongoingReqs == 0) {
            popupSpinner.setVisibility(View.GONE);
        }
    }

    final private void adaptRatingsDisplay() {
        UserRatingsDataResponse userRating = session.getRatingIfExists(SearchSeriesDataResponse.ITEM_TYPE, tvShow.getId().toString());
        EditText input = view.findViewById(R.id.user_rating_input);
        if (userRating != null) {
            input.setText(userRating.getRating().toString());
        }
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    Log.d(LOG_TAG, "coucou");
                    handled = true;
                }
                return handled;
            }
        });


    }

    final private void adaptFavoriteBtnDisplay() {
        Button toggleFavoriteBtn = (Button) view.findViewById(R.id.favoriteHandler);
        if (session.isShowFavorite(tvShowDetails.getId().toString())) {
            toggleFavoriteBtn.setText(getResources().getString(R.string.series_rm_from_favorites));
        } else {
            toggleFavoriteBtn.setText(getResources().getString(R.string.series_add_to_favorites));
        }
        toggleFavoriteBtn.setVisibility(View.VISIBLE);
    }

    final private void loadSerieBasicDetails() {
        TextView seriesName = (TextView) view.findViewById(R.id.series_name);
        seriesName.setText(tvShowDetails.getSeriesName());

        TextView seriesRating = (TextView) view.findViewById(R.id.series_rating);
        seriesRating.setText(tvShowDetails.getRatingsOverview(getResources()));

        TextView seriesStatus = (TextView) view.findViewById(R.id.series_status);
        TextView seriesDisplayDate = (TextView) view.findViewById(R.id.air_display_date);
        seriesDisplayDate.setVisibility(View.GONE);
        String statusPref = getResources().getString(R.string.series_status_prefix)+ " " ;
        switch (tvShowDetails.getStatus().toLowerCase()) {
            case "ended": {
                seriesStatus.setText(statusPref + getResources().getString(R.string.series_status_ended));
                seriesStatus.setTextColor(Color.BLACK);
                break;
            }
            case "continuing": {
                seriesStatus.setText(statusPref + getResources().getString(R.string.series_status_continuing));
                seriesStatus.setTextColor(Color.GREEN);
                seriesDisplayDate.setVisibility(View.VISIBLE);
                seriesDisplayDate.setText(tvShowDetails.getDisplayedDate());
                break;
            }
            default: {
                seriesStatus.setText(statusPref + tvShowDetails.getStatus());
                seriesStatus.setTextColor(Color.LTGRAY);
                break;
            }
        }

        TextView seriesFirstAirDate = (TextView) view.findViewById(R.id.first_aired);
        seriesFirstAirDate.setText(getResources().getString(R.string.series_first_aired_prefix) + " " + tvShowDetails.getFirstAired());

        TextView seriesGenres = (TextView) view.findViewById(R.id.genre);
        if (tvShowDetails.getGenre().size() > 0) {
            seriesGenres.setText(getResources().getString(R.string.series_genres_prefix) + " " + TextUtils.join(", ", tvShowDetails.getGenre()));
        } else {
            seriesGenres.setVisibility(View.GONE);
        }

        TextView seriesNetwork = (TextView) view.findViewById(R.id.network);
        if (tvShowDetails.getNetwork() != null && tvShowDetails.getNetwork().trim().length() > 0 ) {
            seriesNetwork.setText(getResources().getString(R.string.series_network_prefix) + " " + tvShowDetails.getNetwork());
        }  else {
            seriesNetwork.setText(R.string.series_unknown_network);
        }

        TextView seriesOverview = (TextView) view.findViewById(R.id.overview);
        if (tvShowDetails.getOverview() != null && tvShowDetails.getOverview().length() > 0 ) {
            seriesOverview.setText(getResources().getString(R.string.series_overview_prefix) + " " + tvShowDetails.getOverview());
        }  else {
            seriesOverview.setText(getResources().getString(R.string.series_no_overview));
        }

        TextView seriesImdbId = (TextView) view.findViewById(R.id.imdb_id);
        if (tvShowDetails.getImdbId() != null && tvShowDetails.getImdbId().trim().length() > 0 ) {
            seriesImdbId.setText(getResources().getString(R.string.series_imdb_id_prefix) + " " + tvShowDetails.getImdbId());
        }  else {
            seriesImdbId.setVisibility(View.GONE);
        }

        TextView seriesZap2itId = (TextView) view.findViewById(R.id.zap2it_id);
        if (tvShowDetails.getZap2itId() != null && tvShowDetails.getZap2itId().trim().length() > 0 ) {
            seriesZap2itId.setText(getResources().getString(R.string.series_zap2it_id_prefix) + " " + tvShowDetails.getZap2itId());
        }  else {
            seriesZap2itId.setVisibility(View.GONE);
        }


        TextView seriesLastUpdated = (TextView) view.findViewById(R.id.last_updated);
        if (tvShowDetails.getLastUpdated() != null && tvShowDetails.getLastUpdated() > 0 ) {
            seriesLastUpdated.setText(getResources().getString(R.string.series_lastupdated_prefix) + " " + tvShowDetails.getFormatedLastUpdated());
        }  else {
            seriesLastUpdated.setVisibility(View.GONE);
        }

    }

    final private void loadEpisodes(LinearLayout episodesContainer, List<EpisodeDetail> episodes) {
        for (EpisodeDetail episode: episodes) {
            LinearLayout episodeLayout = new LinearLayout(view.getContext());
            episodeLayout.setMinimumHeight(75);
            episodeLayout.setWeightSum(1);
            episodeLayout.setPadding(5, 5, 5, 5);
            episodeLayout.setOrientation(LinearLayout.VERTICAL);

            // ---- Episode number -----
            TextView episodeNumber = new TextView(view.getContext());
            String epNumber = getResources().getString(R.string.episode_ep_prefix);
            if (episode.getAiredEpisodeNumber() != null && episode.getAiredEpisodeNumber() >= 0) {
                epNumber += " " + episode.getAiredEpisodeNumber();
            } else {
                epNumber += " " + getResources().getString(R.string.episode_unknown_season);
            }
            episodeNumber.setText(epNumber);
            episodeNumber.setWidth(view.getWidth());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                episodeNumber.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                episodeNumber.setTextAppearance(Window.FEATURE_CUSTOM_TITLE);
            }
            episodeNumber.setTextColor(Color.BLACK);
            episodeNumber.setPadding(5, 5, 5, 0);
            episodeLayout.addView(episodeNumber);

            TextView episodeName = new TextView(view.getContext());
            if (episode.getEpisodeName() != null && episode.getEpisodeName().length() > 2) {
                episodeName.setText(episode.getEpisodeName());
            } else {
                episodeName.setText(getResources().getString(R.string.episode_unnamed));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                episodeName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            episodeName.setPadding(5, 5, 5, 0);
            episodeLayout.addView(episodeName);

            if (episode.getFirstAired() != null && episode.getFirstAired().length() > 3) {
                TextView episodeFirstDiffusion = new TextView(view.getContext());
                episodeFirstDiffusion.setText(getResources().getString(R.string.series_first_aired_prefix) + " " + episode.getFirstAired());
                episodeFirstDiffusion.setPadding(5, 5, 5, 5);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    episodeFirstDiffusion.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                episodeLayout.addView(episodeFirstDiffusion);
            }

            episodesContainer.addView(episodeLayout);
        }
    }

    final private void loadSeason(String seasonNumber, List<EpisodeDetail> episodes, LinearLayout seasonsContainer) {
        LinearLayout seasonContainer = new LinearLayout(view.getContext());
        seasonContainer.setOrientation(LinearLayout.VERTICAL);
        seasonContainer.setPadding(5, 50, 5, 50);

        TextView title = new TextView(view.getContext());
        String seasonTitle = getResources().getString(R.string.episode_season_prefix);
        if (Integer.parseInt(seasonNumber) > 0) {
            seasonTitle += " " + seasonNumber;
        } else {
            seasonTitle += " " + getResources().getString(R.string.episode_unknown_season);
        }
        title.setText(seasonTitle);
        title.setPadding(5, 15, 5, 15);
        seasonContainer.addView(title);

        LinearLayout separator = new LinearLayout(view.getContext());
        separator.setMinimumHeight(5);
        separator.setBackgroundColor(Color.BLACK);
        separator.setPadding(50, 5, 50, 5);
        seasonContainer.addView(separator);

        HorizontalScrollView seasonWrapper = new HorizontalScrollView(view.getContext());
        seasonWrapper.setPadding(50, 5, 50, 5);

        LinearLayout episodesContainer = new LinearLayout(view.getContext());
        episodesContainer.setMinimumWidth(episodes.size() * 100);
        episodesContainer.setOrientation(LinearLayout.HORIZONTAL);
        episodesContainer.setGravity(Gravity.CENTER);
        episodesContainer.setMinimumHeight(120);
        episodesContainer.setPadding(5, 5, 5, 50);

        loadEpisodes(episodesContainer, episodes);
        seasonWrapper.addView(episodesContainer);
        seasonContainer.addView(seasonWrapper);
        seasonsContainer.addView(seasonContainer);
    }

    final private void fetchSerie() {
        this.showSpinner();
        this.apiSm.getSerie(this.session.getSessionToken(), this.language, tvShow.getId(), new Subscriber<Response<GetSerieResponse>>() {
            @Override
            public void onCompleted() {
                hideSpinner();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LOG_TAG, "error", e);
                hideSpinner();
            }

            @Override
            public void onNext(Response<GetSerieResponse> response) {
                if (response.isSuccessful()) {
                    _neeededResponses -= 1;
                    GetSerieResponse res = response.body();
                    GetSerieDataResponse resData = res.getData();
                    tvShowDetails = resData;
                    loadSerieBasicDetails();
                    if (_neeededResponses == 0) {
                        adaptFavoriteBtnDisplay();
                    }
                    Log.d(LOG_TAG, "yeah mofo");
                } else {
                    Log.d(LOG_TAG, "uh oh, bad hat harry");
                }
            }
        });
    }

    final private void fetchUserFavorites() {
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
                    _neeededResponses -= 1;
                    UserFavoritesResponse userResponse = response.body();
                    session.setUserFavoritesShows(userResponse.getData().getFavorites());
                    if (_neeededResponses == 0) {
                        adaptFavoriteBtnDisplay();
                    }
                } else {
                    Log.d(LOG_TAG, "Failed to fetch user's favorites");
                }
            }
        });
    }

    final private void fetchSeriesEpisodes() {
        this.showSpinner();
        this.apiSm.getSeriesEpisodes(this.session.getSessionToken(), this.language, tvShow.getId(), new Subscriber<Response<GetSeriesEpisodesResponse>>() {
            @Override
            public void onCompleted() {
                hideSpinner();
            }

            @Override
            public void onError(Throwable e) {
                hideSpinner();
                Log.e(LOG_TAG, "niktamereJava", e);
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
                    // Sorting by season
                    TreeMap<String, List<EpisodeDetail>> sorted = new TreeMap<>(episodesBySeasons);
                    Set<Map.Entry<String, List<EpisodeDetail>>> mappings = sorted.entrySet();
                    // Sorting by episodes
                    for(Map.Entry<String, List<EpisodeDetail>> mapping : mappings){
                        Collections.sort(mapping.getValue());
                    }

                    LinearLayout seasonsContainer = view.findViewById(R.id.seasons_container);
                    TextView mainTitle = new TextView(view.getContext());
                    mainTitle.setText(getResources().getString(R.string.episodes_container_title).toUpperCase());
                    mainTitle.setPadding(0, 10, 0, 20);
                    mainTitle.setTextColor(Color.BLACK);
                    seasonsContainer.addView(mainTitle);

                    // Load each season separately
                    for(Map.Entry<String, List<EpisodeDetail>> mapping : mappings){
                        loadSeason(mapping.getKey(), mapping.getValue(), seasonsContainer);
                    }


                } else {
                    Log.d(LOG_TAG, "Failed to fetch serie's episodes");
                }
            }
        });
    }

    final private void fetchData() {
        this.fetchSerie();
        this.fetchUserFavorites();
        this.fetchSeriesEpisodes();
    }

    final private void putFavorite(String tvShowID) {
        this.showSpinner();
        this.apiSm.putUserFavorite(this.session.getSessionToken(), tvShowID, new Subscriber<Response<UserFavoritesResponse>>() {
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
                    adaptFavoriteBtnDisplay();
                } else {
                    Log.d(LOG_TAG, "Failed to update user's favorites");
                }
            }
        });
    }

    final private void deleteFavorite(String tvShowID) {
        this.showSpinner();
        this.apiSm.deleteUserFavorite(this.session.getSessionToken(), tvShowID, new Subscriber<Response<UserFavoritesResponse>>() {
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
                    adaptFavoriteBtnDisplay();
                } else {
                    Log.d(LOG_TAG, "Failed to update user's favorites");
                }
            }
        });
    }

    public void handleFavorite() {
        if (session.isShowFavorite(tvShowDetails.getId().toString())) {
            this.deleteFavorite(tvShowDetails.getId().toString());
        } else {
            this.putFavorite(tvShowDetails.getId().toString());
        }
    }
}
