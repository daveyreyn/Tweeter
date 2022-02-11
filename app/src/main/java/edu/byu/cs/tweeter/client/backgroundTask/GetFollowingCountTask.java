package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {

    private FollowingCountRequest request;

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
        request = new FollowingCountRequest(targetUser.getAlias(), authToken);
    }

    @Override
    protected int runCountTask() {
        FollowingCountResponse response = null;
        try {
            response = getServerFacade().getFollowingCount(request, "/getfollowingcount");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return response.getNumFollowing();
    }
}
