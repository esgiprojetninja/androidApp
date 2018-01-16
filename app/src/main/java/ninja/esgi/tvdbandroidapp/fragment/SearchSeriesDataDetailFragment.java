package ninja.esgi.tvdbandroidapp.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import ninja.esgi.tvdbandroidapp.R;
import ninja.esgi.tvdbandroidapp.model.response.SearchSeriesDataResponse;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class SearchSeriesDataDetailFragment extends DialogFragment {
    public SearchSeriesDataResponse tvShow;

    public SearchSeriesDataDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_series_data_detail, container, true);

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


}
