package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerCountResponse extends Response{

    private int numFollower;

    public FollowerCountResponse(String message){
        super(false, message);
    }

    public FollowerCountResponse() {
        super(true, null);
    }


    public void setNumFollower(int numFollower){
        this.numFollower = numFollower;
    }

    public int getNumFollower(){
        return numFollower;
    }

}
