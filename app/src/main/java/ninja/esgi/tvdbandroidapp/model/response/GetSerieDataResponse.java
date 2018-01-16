package ninja.esgi.tvdbandroidapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetSerieDataResponse {
    @SerializedName("added")
    @Expose
    private String added;

    @SerializedName("airsDayOfWeek")
    @Expose
    private String airsDayOfWeek;

    @SerializedName("airsTime")
    @Expose
    private String airsTime;

    @SerializedName("aliases")
    @Expose
    private List<String> aliases;

    @SerializedName("banner")
    @Expose
    private String banner;

    @SerializedName("firstAired")
    @Expose
    private String firstAired;

    @SerializedName("genre")
    @Expose
    private List<String> genre;

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("imdbId")
    @Expose
    private String imdbId;

    @SerializedName("lastUpdated")
    @Expose
    private Long lastUpdated;

    @SerializedName("network")
    @Expose
    private String network;

    @SerializedName("networkId")
    @Expose
    private String networkId;

    @SerializedName("overview")
    @Expose
    private String overview;

    @SerializedName("rating")
    @Expose
    private String rating;

    @SerializedName("runtime")
    @Expose
    private String runtime;

// #########  Tdvb's API swagger is f*** up. This is a string thank you very much
    @SerializedName("seriesId")
    @Expose
    private String seriesId;

    @SerializedName("seriesName")
    @Expose
    private String seriesName;

    @SerializedName("siteRating")
    @Expose
    private Double siteRating;

    @SerializedName("siteRatingCount")
    @Expose
    private Long siteRatingCount;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("zap2itId")
    @Expose
    private String zap2itId;

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public String getAirsDayOfWeek() {
        return airsDayOfWeek;
    }

    public void setAirsDayOfWeek(String airsDayOfWeek) {
        this.airsDayOfWeek = airsDayOfWeek;
    }

    public String getAirsTime() {
        return airsTime;
    }

    public void setAirsTime(String airsTime) {
        this.airsTime = airsTime;
    }

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

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getNetworkId() {
        return networkId;
    }

    public void setNetworkId(String networkId) {
        this.networkId = networkId;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getSeriesId() {
        return seriesId;
    }

    public void setSeriesId(String seriesId) {
        this.seriesId = seriesId;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public Double getSiteRating() {
        return siteRating;
    }

    public void setSiteRating(Double siteRating) {
        this.siteRating = siteRating;
    }

    public Long getSiteRatingCount() {
        return siteRatingCount;
    }

    public void setSiteRatingCount(Long siteRatingCount) {
        this.siteRatingCount = siteRatingCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getZap2itId() {
        return zap2itId;
    }

    public void setZap2itId(String zap2itId) {
        this.zap2itId = zap2itId;
    }
}
