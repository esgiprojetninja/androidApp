package ninja.esgi.tvdbandroidapp.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserRatingsLinksResponse {
    @SerializedName("first")
    @Expose
    private Long first;

    @SerializedName("last")
    @Expose
    private Long last;

    @SerializedName("next")
    @Expose
    private Long next;

    @SerializedName("previous")
    @Expose
    private Long previous;

    public Long getFirst() {
        return first;
    }

    public void setFirst(Long first) {
        this.first = first;
    }

    public Long getLast() {
        return last;
    }

    public void setLast(Long last) {
        this.last = last;
    }

    public Long getNext() {
        return next;
    }

    public void setNext(Long next) {
        this.next = next;
    }

    public Long getPrevious() {
        return previous;
    }

    public void setPrevious(Long previous) {
        this.previous = previous;
    }
}
