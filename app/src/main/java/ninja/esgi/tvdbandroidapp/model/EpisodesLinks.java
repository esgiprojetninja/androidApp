package ninja.esgi.tvdbandroidapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EpisodesLinks {
    @SerializedName("first")
    @Expose
    private Long first;

    @SerializedName("last")
    @Expose
    private Long last;

    @SerializedName("next")
    @Expose
    private Long next;

    @SerializedName("prev")
    @Expose
    private Long prev;

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

    public Long getPrev() {
        return prev;
    }

    public void setPrev(Long prev) {
        this.prev = prev;
    }
}
