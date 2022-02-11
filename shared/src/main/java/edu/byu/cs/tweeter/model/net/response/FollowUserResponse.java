package edu.byu.cs.tweeter.model.net.response;

public class FollowUserResponse extends Response {

    public FollowUserResponse(String message) {
        super(false, message);
    }

    public FollowUserResponse() {
        super(true, null);
    }
}
