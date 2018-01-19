package ninja.esgi.tvdbandroidapp.fragment;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ninja.esgi.tvdbandroidapp.R;
import ninja.esgi.tvdbandroidapp.activity.UpdatedSeriesDetailActivity;
import ninja.esgi.tvdbandroidapp.activity.UpdatedSeriesListActivity;
import ninja.esgi.tvdbandroidapp.activity.dummy.DummyContent;
import ninja.esgi.tvdbandroidapp.model.UpdatedSerie;
import ninja.esgi.tvdbandroidapp.model.response.GetSerieDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.GetSerieResponse;
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

            Activity activity = this.getActivity();
            appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            fetchSeries();
        }
    }

    private void loadData() {
        if (appBarLayout != null) {
            String name = serieData.getSeriesName();
            if ( name == null || name.length() <= 3)
                name = getString(R.string.unknown_tvshow_name);

            if (serieData.getFirstAired() != null && serieData.getFirstAired().contains("-"))
                name += " - " + serieData.getFirstAired().split("-")[0];
            appBarLayout.setTitle(name);
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
                    loadData();
                    Log.d(LOG_TAG, "yeah mofo");
                } else {
                    Log.d(LOG_TAG, "uh oh, bad hat harry");
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.updatedseries_detail, container, false);
//
//        // Show the dummy content as text in a TextView.
////        if (serieData != null) {
////            ((TextView) rootView.findViewById(R.id.updatedseries_detail)).setText(serieData.getSeriesName());
////        }
//
        return rootView;
    }
}
