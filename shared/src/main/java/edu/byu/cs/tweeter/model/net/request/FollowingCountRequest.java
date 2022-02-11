package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingCountRequest {
    //private User user;
    private String userAlias;
    private AuthToken authToken;


    private FollowingCountRequest(){}

    public FollowingCountRequest(String user, AuthToken authToken){
        this.authToken = authToken;
        this.userAlias = user;
    }

    public String getUserAlias(){
        return userAlias;
    }
    public AuthToken getAuthToken(){
        return authToken;
    }

    public void setUserAlias(String user){
        this.userAlias = user;
    }
    public void setAuthToken(AuthToken authToken){
        this.authToken = authToken;
    }

    @Override
    public String toString() {
        return "FollowingCountRequest{" +
                "user=" + userAlias +
                ", authToken=" + authToken +
                '}';
    }
}
