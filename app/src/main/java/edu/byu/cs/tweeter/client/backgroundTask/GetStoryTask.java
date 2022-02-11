package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
//import edu.byu.cs.tweeter.util.Pair;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.model.util.Pair;


/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {
    private StoryRequest request;

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
        request = new StoryRequest(targetUser, limit, lastStatus, authToken);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        StoryResponse response = null;
        try{
            response = getServerFacade().getStory(request, "/getstory");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        Pair<List<Status>, Boolean> pageOfItems = new Pair<>(response.getStatuses(), response.getHasMorePages());
        return pageOfItems;
    }
}