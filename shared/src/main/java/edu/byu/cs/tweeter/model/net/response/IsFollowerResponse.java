package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends Response {
    private boolean following;

    public IsFollowerResponse(String message){
        super(false, message);
    }

    public IsFollowerResponse(boolean following){
        super(true, null);
        this.following = following;
    }

    public boolean isFollowing() {
        return following;
    }
}

