package ninja.esgi.tvdbandroidapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ninja.esgi.tvdbandroidapp.model.UpdatedSerie;

public class UpdatedSeriesResponse {
    @SerializedName("data")
    @Expose
    private List<UpdatedSerie> data;

    @SerializedName("errors")
    @Expose
    private GenericErrorsResponse errors;

    public List<UpdatedSerie> getData() {
        return data;
    }

    public void setData(List<UpdatedSerie> data) {
        this.data = data;
    }

    public GenericErrorsResponse getErrors() {
        return errors;
    }

    public void setErrors(GenericErrorsResponse errors) {
        this.errors = errors;
    }
}
