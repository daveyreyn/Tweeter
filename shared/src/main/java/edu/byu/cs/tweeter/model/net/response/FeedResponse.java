package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.Status;
import java.util.List;

public class FeedResponse extends PagedResponse {

    private List<Status> statuses;

    public FeedResponse(String message) {
        super(false, message, false);
    }

    public FeedResponse(List<Status> statuses, boolean hasMorePages) {
        super(true, hasMorePages);
        this.statuses = statuses;
    }

    public List<Status> getStatuses(){
        return statuses;
    }

    public void setStatuses(List<Status> statuses){
        this.statuses = statuses;
    }

}