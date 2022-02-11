package edu.byu.cs.tweeter.client.backgroundTask;


import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.util.Pair;
//import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedStatusTask {

    private FeedRequest request;

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
        request = new FeedRequest(targetUser, limit, lastStatus, authToken);
        setURLPath("/getfeed");
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        FeedResponse response = null;
        try {
            response = getServerFacade().getFeed(request, "/getfeed");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        Pair<List<Status>, Boolean> pageOfItems = new Pair<>(response.getStatuses(), response.getHasMorePages());
        return pageOfItems;
    }
}
