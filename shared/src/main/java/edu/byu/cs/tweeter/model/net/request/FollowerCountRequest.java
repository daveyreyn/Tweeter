package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerCountRequest {
    //private User user;
    private String userAlias;
    private AuthToken authToken;

    private  FollowerCountRequest() {}
    public FollowerCountRequest(String user, AuthToken authToken){
        this.userAlias = user;
        this.authToken = authToken;
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
        return "FollowerCountRequest{" +
                "user=" + userAlias +
                ", authToken=" + authToken +
                '}';
    }
}
