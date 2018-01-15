package ninja.esgi.tvdbandroidapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchSeriesResponse {
    @SerializedName("aliases")
    @Expose
    private List<String> aliases;

    @SerializedName("banner")
    @Expose
    private String banner;

    @SerializedName("firstAired")
    @Expose
    private String firstAired;

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("network")
    @Expose
    private Long network;

    @SerializedName("overview")
    @Expose
    private Long overview;

    @SerializedName("seriesName")
    @Expose
    private Long seriesName;

    @SerializedName("status")
    @Expose
    private Long status;

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getFirstAired() {
        return firstAired;
    }

    public void setFirstAired(String firstAired) {
        this.firstAired = firstAired;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNetwork() {
        return network;
    }

    public void setNetwork(Long network) {
        this.network = network;
    }

    public Long getOverview() {
        return overview;
    }

    public void setOverview(Long overview) {
        this.overview = overview;
    }

    public Long getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(Long seriesName) {
        this.seriesName = seriesName;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}
