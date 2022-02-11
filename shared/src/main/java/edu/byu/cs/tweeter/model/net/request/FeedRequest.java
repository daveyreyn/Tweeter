package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedRequest {
    private User user;
    private int limit;
    private Status lastStatus;
    private AuthToken authToken;

    private FeedRequest() {}

    public FeedRequest(User user, int limit, Status lastStatus, AuthToken authToken) {
        this.user = user;
        this.limit = limit;
        this.lastStatus = lastStatus;
        this.authToken = authToken;
    }

    public User getUser() { return user; }

    public int getLimit() { return limit; }

    public Status getLastStatus() { return lastStatus; }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setLastStatus(Status lastStatus) {
        this.lastStatus = lastStatus;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
