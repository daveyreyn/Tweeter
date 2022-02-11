package edu.byu.cs.tweeter.model.net.request;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class BatchShareStatusRequest {
    private List<User> followers;
    private Status status;
    private AuthToken authToken;

    BatchShareStatusRequest(){}

    public BatchShareStatusRequest(List<User> followers, Status status, AuthToken authToken){
        this.followers = followers;
        this.status = status;
        this.authToken = authToken;
    }

    public Status getStatus(){
        return status;
    }
    public void setStatus(Status status){
        this.status = status;
    }

    public AuthToken getAuthToken(){
        return authToken;
    }
    public void setAuthToken(AuthToken authToken){
        this.authToken = authToken;
    }

    public List<User> getFollowers(){
        return followers;
    }

    public void setFollowers(List<User> followers){
        this.followers = followers;
    }

}

