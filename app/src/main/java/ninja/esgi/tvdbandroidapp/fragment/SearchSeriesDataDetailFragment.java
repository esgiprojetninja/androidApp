package ninja.esgi.tvdbandroidapp.fragment;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Spinner;
import android.widget.TextView;

import ninja.esgi.tvdbandroidapp.R;
import ninja.esgi.tvdbandroidapp.model.response.GetSerieDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.GetSerieResponse;
import ninja.esgi.tvdbandroidapp.model.response.SearchSeriesDataResponse;
import ninja.esgi.tvdbandroidapp.networkops.ApiServiceManager;
import ninja.esgi.tvdbandroidapp.session.SessionStorage;
import retrofit2.Response;
import rx.Subscriber;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class SearchSeriesDataDetailFragment extends DialogFragment {
    final private static String LOG_TAG = "SearchSeriesDDFragment";
    public SearchSeriesDataResponse tvShow;
    public String language;
    private int _ongoingReqs = 0;
    private SessionStorage session = null;
    private ApiServiceManager apiSm = null;
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
        this.fetchData();

        // @TODO GET series/{id}/actors
        // @TODO GET series/{id}/episodes
        // @TODO GET series/{id}/images
        // @TODO GET episodes/{id} on click ?
        // @TODO GET episodes/{id} on click ?
        // @TODO GET /user/favorites
        // @TODO GET /user/ratings
        // @TODO PUT /user/ratings/{itemType}/{itemId}/{itemRating}
        // @TODO DELETE /user/ratings/{itemType}/{itemId}
        // @TODO PUT /user/favorites/{id}
        // @TODO DELETE /user/favorites/{id}

        return view;
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

    final private void loadSerieBasicDetails(GetSerieDataResponse data) {
        TextView seriesName = (TextView) view.findViewById(R.id.series_name);
        seriesName.setText(data.getSeriesName());

        TextView seriesRating = (TextView) view.findViewById(R.id.series_rating);
        seriesRating.setText(data.getRatingsOverview(getResources()));

        TextView seriesStatus = (TextView) view.findViewById(R.id.series_status);
        TextView seriesDisplayDate = (TextView) view.findViewById(R.id.air_display_date);
        seriesDisplayDate.setVisibility(View.GONE);
        String statusPref = getResources().getString(R.string.series_status_prefix)+ " " ;
        switch (data.getStatus().toLowerCase()) {
            case "ended": {
                seriesStatus.setText(statusPref + getResources().getString(R.string.series_status_ended));
                seriesStatus.setTextColor(Color.BLACK);
                break;
            }
            case "continuing": {
                seriesStatus.setText(statusPref + getResources().getString(R.string.series_status_continuing));
                seriesStatus.setTextColor(Color.GREEN);
                seriesDisplayDate.setVisibility(View.VISIBLE);
                seriesDisplayDate.setText(data.getDisplayedDate());
                break;
            }
            default: {
                seriesStatus.setText(statusPref + data.getStatus());
                seriesStatus.setTextColor(Color.LTGRAY);
                break;
            }
        }

        TextView seriesFirstAirDate = (TextView) view.findViewById(R.id.first_aired);
        seriesFirstAirDate.setText(getResources().getString(R.string.series_first_aired_prefix) + " " + data.getFirstAired());

        TextView seriesGenres = (TextView) view.findViewById(R.id.genre);
        if (data.getGenre().size() > 0) {
            seriesGenres.setText(getResources().getString(R.string.series_genres_prefix) + " " + TextUtils.join(", ", data.getGenre()));
        } else {
            seriesGenres.setVisibility(View.GONE);
        }

        TextView seriesNetwork = (TextView) view.findViewById(R.id.network);
        if (data.getNetwork() != null && data.getNetwork().trim().length() > 0 ) {
            seriesNetwork.setText(getResources().getString(R.string.series_network_prefix) + " " + data.getNetwork());
        }  else {
            seriesNetwork.setText(R.string.series_unknown_network);
        }

        TextView seriesOverview = (TextView) view.findViewById(R.id.overview);
        if (data.getOverview() != null && data.getOverview().length() > 0 ) {
            seriesOverview.setText(getResources().getString(R.string.series_overview_prefix) + " " + data.getOverview());
        }  else {
            seriesOverview.setText(getResources().getString(R.string.series_no_overview));
        }

        TextView seriesImdbId = (TextView) view.findViewById(R.id.imdb_id);
        if (data.getImdbId() != null && data.getImdbId().trim().length() > 0 ) {
            seriesImdbId.setText(getResources().getString(R.string.series_imdb_id_prefix) + " " + data.getImdbId());
        }  else {
            seriesImdbId.setVisibility(View.GONE);
        }

        TextView seriesZap2itId = (TextView) view.findViewById(R.id.zap2it_id);
        if (data.getZap2itId() != null && data.getZap2itId().trim().length() > 0 ) {
            seriesZap2itId.setText(getResources().getString(R.string.series_zap2it_id_prefix) + " " + data.getZap2itId());
        }  else {
            seriesZap2itId.setVisibility(View.GONE);
        }


        TextView seriesLastUpdated = (TextView) view.findViewById(R.id.last_updated);
        if (data.getLastUpdated() != null && data.getLastUpdated() > 0 ) {
            seriesLastUpdated.setText(getResources().getString(R.string.series_lastupdated_prefix) + " " + data.getFormatedLastUpdated());
        }  else {
            seriesLastUpdated.setVisibility(View.GONE);
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
                    GetSerieResponse res = response.body();
                    GetSerieDataResponse resData = res.getData();
                    loadSerieBasicDetails(resData);
                    Log.d(LOG_TAG, "yeah mofo");
                } else {
                    Log.d(LOG_TAG, "uh oh, bad hat harry");
                }
            }
        });
    }

    final private void fetchData() {
        this.fetchSerie();
    }
}
