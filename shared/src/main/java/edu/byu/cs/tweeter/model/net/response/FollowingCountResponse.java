package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;

public class FollowingCountResponse extends Response {

    private int numFollowing;

    public FollowingCountResponse(String message){
        super(false, message);
    }

    public FollowingCountResponse(){
        super(true, null);
    }
        /*
    public User getUser() {
        return user;
    }

    /**
     * Returns the auth token.
     *
     * @return the auth token.
     */
        /*
    public AuthToken getAuthToken() {
        return authToken;
    } */

    public void setNumFollowing(int numFollowing){
        this.numFollowing = numFollowing;
    }

    public int getNumFollowing(){
        return numFollowing;
    }

}
