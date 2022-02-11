package edu.byu.cs.tweeter.client.model.service;
import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryService {


    public void getStory(AuthToken authToken,
                         User targetUser, int limit, Status status, ServicePageObserver observer ){
        GetStoryTask getStoryTask = new GetStoryTask(authToken, targetUser, limit, status, new GetStoryHandler(observer));
        ExecuteService executeService = new ExecuteService();
        executeService.execute(getStoryTask);
    }




    private class GetStoryHandler extends BackgroundTaskHandler<ServicePageObserver> {



        public GetStoryHandler(ServicePageObserver observer){
            super(observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return null;
        }

        @Override
        protected void handleSuccessMessage(ServicePageObserver observer, Bundle data) {
            boolean hasMorePages = data.getBoolean(GetStoryTask.MORE_PAGES_KEY);
            List<Status> statuses = (List<Status>) data.getSerializable(GetStoryTask.ITEMS_KEY);
            observer.getPageSucceeded(statuses, hasMorePages);
        }
    }
}
