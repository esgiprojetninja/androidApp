package ninja.esgi.tvdbandroidapp.model;

import java.util.HashMap;

public class Search {
    private String name;
    private String imdbId;
    private String zap2itId;
    private String language;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getZap2itId() {
        return zap2itId;
    }

    public void setZap2itId(String zap2itId) {
        this.zap2itId = zap2itId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean checkIsValid() {
        return (this.name != null
            && this.name.length() > 0) ||
            (this.imdbId != null
            && this.imdbId.length() > 0) ||
            (this.zap2itId != null
            && this.zap2itId.length() > 0);
    }

    public HashMap<String, String> getQueriesMap() {
        HashMap<String, String> queriesMap = new HashMap<String, String>();
        if (this.name != null && this.name.length() > 0) {
            queriesMap.put("name", this.name);
        }
        if (this.imdbId != null && this.imdbId.length() > 0) {
            queriesMap.put("imdbId", this.imdbId);
        }
        if (this.zap2itId != null && this.zap2itId.length() > 0) {
            queriesMap.put("zap2itId", this.zap2itId);
        }
        return queriesMap;
    }

}
