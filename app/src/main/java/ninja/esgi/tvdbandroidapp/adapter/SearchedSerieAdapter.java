package ninja.esgi.tvdbandroidapp.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ninja.esgi.tvdbandroidapp.activity.SearchSeriesActivity;
import ninja.esgi.tvdbandroidapp.model.response.SearchSeriesDataResponse;


public class SearchedSerieAdapter extends BaseAdapter {
    final private static String LOG_TAG = "SearchedSerieAdapter";
    private List<SearchSeriesDataResponse> seriesData;
    private SearchSeriesActivity ssActivity;
    private LayoutInflater inflater;
    public SearchSeriesDataResponse clickedTvShow;

    public SearchedSerieAdapter(SearchSeriesActivity ssActivity, List<SearchSeriesDataResponse> seriesData) {
        this.seriesData = seriesData;
        this.ssActivity = ssActivity;
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
        final SearchSeriesDataResponse ssdr = getItem(position);
        userViewHolder.textView1.setText(ssdr.getSeriesName());
        if ( ssdr.getFirstAired() != null && ssdr.getFirstAired().trim().length() > 0 ) {
            userViewHolder.textView2.setText(ssdr.getFirstAired().split("-")[0]);
        } else {
            userViewHolder.textView2.setText("Unknown release date");
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ssActivity.clickCallback(ssdr);
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
