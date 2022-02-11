package edu.byu.cs.tweeter.model.net.response;

public class UnfollowUserResponse extends Response {
    public UnfollowUserResponse(String message) {
        super(false, message);
    }

    public UnfollowUserResponse() {
        super(true, null);
    }
}
