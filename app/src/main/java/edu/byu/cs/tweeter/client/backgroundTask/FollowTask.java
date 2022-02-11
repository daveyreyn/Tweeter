package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.client.backgroundTask.AuthorizedTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowUserRequest;
import edu.byu.cs.tweeter.model.net.response.FollowUserResponse;

/**
 * Background task that establishes a following relationship between two users.
 */
public class FollowTask extends AuthorizedTask {
    /**
     * The user that is being followed.
     */
    private final User followee;
    private FollowUserRequest request;

    public FollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
        User currUser = Cache.getInstance().getCurrUser();
        request = new FollowUserRequest(currUser,followee, authToken);
    }

    @Override
    protected void runTask() {
        FollowUserResponse response = null;
        try {
            response = getServerFacade().followUser(request, "/followuser");

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void loadBundle(Bundle msgBundle) {
        // Nothing to load
    }
}
