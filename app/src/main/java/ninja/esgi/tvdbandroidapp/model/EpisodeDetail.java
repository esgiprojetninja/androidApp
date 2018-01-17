package ninja.esgi.tvdbandroidapp.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EpisodeDetail implements Comparable<EpisodeDetail> {
    @SerializedName("absoluteNumber")
    @Expose
    private Long absoluteNumber;

    @SerializedName("airedEpisodeNumber")
    @Expose
    private Long airedEpisodeNumber;

    @SerializedName("airedSeason")
    @Expose
    private Long airedSeason;

    @SerializedName("airedSeasonID")
    @Expose
    private Long airedSeasonID;

    @SerializedName("dvdEpisodeNumber")
    @Expose
    private Long dvdEpisodeNumber;

    @SerializedName("dvdSeason")
    @Expose
    private Long dvdSeason;

    @SerializedName("episodeName")
    @Expose
    private String episodeName;

    @SerializedName("firstAired")
    @Expose
    private String firstAired;

    @SerializedName("id")
    @Expose
    private Long id;

    @SerializedName("lastUpdated")
    @Expose
    private Long lastUpdated;

    @SerializedName("overview")
    @Expose
    private String overview;

    public Long getAbsoluteNumber() {
        return absoluteNumber;
    }

    public void setAbsoluteNumber(Long absoluteNumber) {
        this.absoluteNumber = absoluteNumber;
    }

    public Long getAiredEpisodeNumber() {
        return airedEpisodeNumber;
    }

    public void setAiredEpisodeNumber(Long airedEpisodeNumber) {
        this.airedEpisodeNumber = airedEpisodeNumber;
    }

    public Long getAiredSeason() {
        return airedSeason;
    }

    public void setAiredSeason(Long airedSeason) {
        this.airedSeason = airedSeason;
    }

    public Long getAiredSeasonID() {
        return airedSeasonID;
    }

    public void setAiredSeasonID(Long airedSeasonID) {
        this.airedSeasonID = airedSeasonID;
    }

    public Long getDvdEpisodeNumber() {
        return dvdEpisodeNumber;
    }

    public void setDvdEpisodeNumber(Long dvdEpisodeNumber) {
        this.dvdEpisodeNumber = dvdEpisodeNumber;
    }

    public Long getDvdSeason() {
        return dvdSeason;
    }

    public void setDvdSeason(Long dvdSeason) {
        this.dvdSeason = dvdSeason;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public void setEpisodeName(String episodeName) {
        this.episodeName = episodeName;
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

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    @Override
    public int compareTo(@NonNull EpisodeDetail episodeDetail) {
        if (this == null) return 1;
        if (episodeDetail == null) return -1;
        if (this.absoluteNumber == null)
            return 1;
        if (episodeDetail.getAbsoluteNumber() == null)
            return -1;

        return this.getAbsoluteNumber().compareTo(episodeDetail.getAbsoluteNumber());
    }
}
