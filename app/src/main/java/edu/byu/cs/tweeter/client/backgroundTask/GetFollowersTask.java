package edu.byu.cs.tweeter.client.backgroundTask;


import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
//import edu.byu.cs.tweeter.util.Pair;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.util.Pair;


/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {

    private FollowerRequest request;

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollower, messageHandler);
        String lastFollowerAlias = null;
        if(lastFollower != null){
            lastFollowerAlias = lastFollower.getAlias();
        }
        request = new FollowerRequest(authToken, targetUser.getAlias(), limit, lastFollowerAlias);

        setURLPath("/getfollowers");
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() {
        FollowerResponse response = null;
        try {
            response = getServerFacade().getFollowers(request, "/getfollowers");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        Pair<List<User>, Boolean> pageOfItems = new Pair<>(response.getFollowers(), response.getHasMorePages());
        return pageOfItems;

    }
}
