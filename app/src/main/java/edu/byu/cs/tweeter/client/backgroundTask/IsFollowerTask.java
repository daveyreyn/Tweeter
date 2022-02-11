package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.util.Random;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthorizedTask {

    private IsFollowerRequest request;

    public static final String IS_FOLLOWER_KEY = "is-follower";

    /**
     * The alleged follower.
     */
    private final User follower;

    /**
     * The alleged followee.
     */
    private final User followee;

    private boolean isFollower;

    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.follower = follower;
        this.followee = followee;
        request = new IsFollowerRequest(follower, followee, authToken);
    }

    @Override
    protected void runTask() {

        IsFollowerResponse response = null;
        try {
            response = getServerFacade().isFollower(request, "/isfollower");
            isFollower = response.isFollowing();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void loadBundle(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);
    }
}