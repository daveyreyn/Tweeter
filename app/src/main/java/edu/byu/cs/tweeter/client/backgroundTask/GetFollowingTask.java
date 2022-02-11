package edu.byu.cs.tweeter.client.backgroundTask;


import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.util.Pair;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;


/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends PagedUserTask {

    private FollowingRequest request;

    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                            Handler messageHandler) {

        super(authToken, targetUser, limit, lastFollowee, messageHandler);
        String lastFolloweeAlias = null;
        if(lastFollowee != null){
            lastFolloweeAlias = lastFollowee.getAlias();
        }

        request = new FollowingRequest(authToken, targetUser.getAlias(), limit, lastFolloweeAlias);
        //setReq(request);
        //setURLPath("/getfollowing");
    }



    @Override
    protected Pair<List<User>, Boolean> getItems() {
        FollowingResponse response = null;
        try {

            response = getServerFacade().getFollowees(request, "/getfollowing");
        }
        catch (Exception e){

        }

        Pair<List<User>, Boolean> pageOfItems = new Pair<>(response.getFollowees(), response.getHasMorePages());



        //return getFakeData().getPageOfUsers(getLastItem(), getLimit(), getTargetUser());
        return pageOfItems;
    }
}
