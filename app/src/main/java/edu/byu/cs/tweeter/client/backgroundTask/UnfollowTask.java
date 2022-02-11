package edu.byu.cs.tweeter.client.backgroundTask;


import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.UnfollowUserRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowUserResponse;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthorizedTask {

    /**
     * The user that is being followed.
     */
    private final User followee;
    private UnfollowUserRequest request;

    public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.followee = followee;
        User currUser = Cache.getInstance().getCurrUser();
        request = new UnfollowUserRequest(currUser,followee, authToken);
    }

    @Override
    protected void runTask() {
        UnfollowUserResponse response = null;
        try {
            response = getServerFacade().unfollowUser(request, "/unfollowuser");

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
