package ninja.esgi.tvdbandroidapp.model.response;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ninja.esgi.tvdbandroidapp.model.EpisodeDetail;
import ninja.esgi.tvdbandroidapp.model.EpisodesLinks;

public class GetSeriesEpisodesResponse {
    @SerializedName("links")
    @Expose
    private EpisodesLinks links;

    @SerializedName("data")
    @Expose
    private List<EpisodeDetail> data;

    @SerializedName("errors")
    @Expose
    private GenericErrorsResponse errors;

    public EpisodesLinks getLinks() {
        return links;
    }

    public void setLinks(EpisodesLinks links) {
        this.links = links;
    }

    public List<EpisodeDetail> getData() {
        return data;
    }

    public void setData(List<EpisodeDetail> data) {
        this.data = data;
    }

    public GenericErrorsResponse getErrors() {
        return errors;
    }

    public void setErrors(GenericErrorsResponse errors) {
        this.errors = errors;
    }
}
