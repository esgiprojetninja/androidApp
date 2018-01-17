package ninja.esgi.tvdbandroidapp.fragment;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ninja.esgi.tvdbandroidapp.R;
import ninja.esgi.tvdbandroidapp.activity.SearchSeriesActivity;
import ninja.esgi.tvdbandroidapp.model.EpisodeDetail;
import ninja.esgi.tvdbandroidapp.model.response.GetSerieDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.GetSerieResponse;
import ninja.esgi.tvdbandroidapp.model.response.GetSeriesEpisodesResponse;
import ninja.esgi.tvdbandroidapp.model.response.SearchSeriesDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserFavoritesResponse;
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

        // @TODO GET series/{id}/actors
        // @TODO GET series/{id}/episodes
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
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
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

    final private void loadEpisodes(List<EpisodeDetail> episodes) {
        LinearLayout horizontalScrollContainer = (LinearLayout) view.findViewById(R.id.scrollViewSubContainer);
        for (EpisodeDetail episode: episodes) {
            LinearLayout test = new LinearLayout(view.getContext());
            test.setMinimumWidth(275);
            test.setMinimumHeight(150);
            test.setWeightSum(1);
            test.setPadding(5, 5, 5, 5);
            test.setOrientation(LinearLayout.VERTICAL);

            // ---- Episode number -----
            TextView text = new TextView(view.getContext());
            String epNumber = getResources().getString(R.string.episode_season_prefix);
            if (episode.getAiredSeason() != null && episode.getAiredSeason() > 0) {
                epNumber += " " + episode.getAiredSeason();
            } else {
                epNumber += " " + getResources().getString(R.string.episode_unknown_season);
            }
            epNumber += " - " + getResources().getString(R.string.episode_ep_prefix);
            if (episode.getAbsoluteNumber() != null && episode.getAbsoluteNumber() >= 0) {
                epNumber += " " + episode.getAbsoluteNumber();
            } else {
                epNumber += " " + getResources().getString(R.string.episode_unknown_season);
            }
            text.setText(epNumber);
            text.setWidth(view.getWidth());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            test.addView(text);

            horizontalScrollContainer.addView(test);
        }
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
            }

            @Override
            public void onNext(Response<GetSeriesEpisodesResponse> response) {
                if (response.isSuccessful()) {
                    GetSeriesEpisodesResponse episodes = response.body();
                    List<EpisodeDetail> episodesList = episodes.getData();
//                    Collections.sort(episodesList, new Comparator<EpisodeDetail>() {
//                        @Override
//                        public int compare(final EpisodeDetail ep1, final EpisodeDetail ep2) {
//                            return ep1.getAbsoluteNumber().compareTo(ep2.getAbsoluteNumber());
//                        }
//                    });
                    loadEpisodes(episodesList);
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
