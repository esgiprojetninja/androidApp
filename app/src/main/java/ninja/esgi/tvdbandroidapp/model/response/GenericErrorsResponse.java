package ninja.esgi.tvdbandroidapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GenericErrorsResponse {
    @SerializedName("invalidFilters")
    @Expose
    private List<String> invalidFilters = null;

    @SerializedName("invalidLanguage")
    @Expose
    private String invalidLanguage = null;

    @SerializedName("invalidQueryParams")
    @Expose
    private List<String> invalidQueryParams = null;

    public List<String> getInvalidFilters() {
        return invalidFilters;
    }

    public void setInvalidFilters(List<String> invalidFilters) {
        this.invalidFilters = invalidFilters;
    }

    public String getInvalidLanguage() {
        return invalidLanguage;
    }

    public void setInvalidLanguage(String invalidLanguage) {
        this.invalidLanguage = invalidLanguage;
    }

    public List<String> getInvalidQueryParams() {
        return invalidQueryParams;
    }

    public void setInvalidQueryParams(List<String> invalidQueryParams) {
        this.invalidQueryParams = invalidQueryParams;
    }
}
