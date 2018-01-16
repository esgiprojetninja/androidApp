package ninja.esgi.tvdbandroidapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetSerieResponse {
    @SerializedName("data")
    @Expose
    private GetSerieDataResponse data;

    @SerializedName("errors")
    @Expose
    private GenericErrorsResponse errors;

    public GetSerieDataResponse getData() {
        return data;
    }

    public void setData(GetSerieDataResponse data) {
        this.data = data;
    }

    public GenericErrorsResponse getErrors() {
        return errors;
    }

    public void setErrors(GenericErrorsResponse errors) {
        this.errors = errors;
    }
}
