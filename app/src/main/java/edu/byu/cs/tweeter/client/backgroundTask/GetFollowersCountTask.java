package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {

    private FollowerCountRequest request;

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
        request = new FollowerCountRequest(targetUser.getAlias(), authToken);
    }

    @Override
    protected int runCountTask() {
        FollowerCountResponse response = null;
        try {
            response = getServerFacade().getFollowerCount(request, "/getfollowercount");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        return response.getNumFollower();
    }
}
