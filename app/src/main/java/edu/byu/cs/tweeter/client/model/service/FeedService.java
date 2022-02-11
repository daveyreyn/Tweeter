package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedService {

    public void GetFeed(AuthToken authToken,
                        User targetUser, int limit, Status status, ServicePageObserver observer ){
        GetFeedTask getFeedTask = new GetFeedTask(authToken, targetUser, limit, status, new GetFeedHandler(observer) {
        });
        ExecuteService executeService = new ExecuteService();
        executeService.execute(getFeedTask);
    }

    /**
     * Message handler (i.e., observer) for GetFeedTask.
     */

    private class GetFeedHandler extends BackgroundTaskHandler<ServicePageObserver> {

        public GetFeedHandler(ServicePageObserver observer){
            super(observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return null;
        }

        @Override
        protected void handleSuccessMessage(ServicePageObserver observer, Bundle data) {
            boolean hasMorePages = data.getBoolean(GetFeedTask.MORE_PAGES_KEY);
            List<Status> statuses = (List<Status>) data.getSerializable(GetFeedTask.ITEMS_KEY);
            observer.getPageSucceeded(statuses, hasMorePages);

        }


    }

}
