package ninja.esgi.tvdbandroidapp.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ninja.esgi.tvdbandroidapp.R;
import ninja.esgi.tvdbandroidapp.activity.SearchSeriesActivity;
import ninja.esgi.tvdbandroidapp.model.response.SearchSeriesDataResponse;
import ninja.esgi.tvdbandroidapp.model.response.UserRatingsDataResponse;
import ninja.esgi.tvdbandroidapp.session.SessionStorage;


public class SearchedSerieAdapter extends BaseAdapter {
    final private static String LOG_TAG = "SearchedSerieAdapter";
    private List<SearchSeriesDataResponse> seriesData;
    private SearchSeriesActivity ssActivity;
    private LayoutInflater inflater;
    public SearchSeriesDataResponse clickedTvShow;
    private SessionStorage session;

    public SearchedSerieAdapter(SearchSeriesActivity ssActivity, List<SearchSeriesDataResponse> seriesData, SessionStorage session) {
        this.seriesData = seriesData;
        this.ssActivity = ssActivity;
        this.session = session;
        this.inflater = (LayoutInflater) ssActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return seriesData != null ? seriesData.size() : 0;
    }

    @Override
    public SearchSeriesDataResponse getItem(int i) {
        return seriesData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        SearchSeriesDataViewHolder userViewHolder = null;
        if(convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_2, null);
            userViewHolder = new SearchSeriesDataViewHolder();
            userViewHolder.textView1 = (TextView) convertView.findViewById(android.R.id.text1);
            userViewHolder.textView2 = (TextView) convertView.findViewById(android.R.id.text2);
            convertView.setTag(userViewHolder);
        } else {
            userViewHolder = (SearchSeriesDataViewHolder) convertView.getTag();
        }
        final SearchSeriesDataResponse searchSerieData = getItem(position);
        String title = searchSerieData.getSeriesName();
        if ( session.isShowFavorite(searchSerieData.getId().toString())) {
            title += " (favorite)";
        }
        userViewHolder.textView1.setText(title);

        String subtitle;
        if ( searchSerieData.getFirstAired() != null && searchSerieData.getFirstAired().trim().length() > 0 ) {
            subtitle = searchSerieData.getFirstAired().split("-")[0];
        } else {
           subtitle = "Unknown release date";
        }

        final UserRatingsDataResponse userRatingsDataResponse = session.getRatingIfExists(SearchSeriesDataResponse.ITEM_TYPE, searchSerieData.getId().toString());
        if (userRatingsDataResponse != null) {
            subtitle += "     -     (" + ssActivity.getResources().getString(R.string.user_grade_prefix) + userRatingsDataResponse.getRating() + ")";
        }
        userViewHolder.textView2.setText(subtitle);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ssActivity.clickCallback(searchSerieData);
            }
        });
        return convertView;
    }

    @Nullable
    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }


    static class SearchSeriesDataViewHolder {
        public TextView textView1;
        public TextView textView2;
    }
}
