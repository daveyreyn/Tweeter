package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowerRequest {
    private User follower;
    private User followee;
    private AuthToken authToken;

    private IsFollowerRequest(){}

    public IsFollowerRequest(User follower, User followee, AuthToken authToken){
        this.follower = follower;
        this.followee = followee;
        this.authToken = authToken;
    }

    public User getFollower() {
        return follower;
    }

    public User getFollowee() {
        return followee;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public void setFollowee(User followee) {
        this.followee = followee;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }
}
