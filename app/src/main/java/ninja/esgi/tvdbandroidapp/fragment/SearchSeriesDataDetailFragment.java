package ninja.esgi.tvdbandroidapp.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Spinner;

import ninja.esgi.tvdbandroidapp.R;
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
        this.loadDatas();

        // @TODO GET series/{id}
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
                    Log.d(LOG_TAG, "yeah mofo");
                } else {
                    Log.d(LOG_TAG, "uh oh, bad hat harry");
                }
            }
        });
    }

    final private void loadDatas() {
        this.fetchSerie();
    }
}
