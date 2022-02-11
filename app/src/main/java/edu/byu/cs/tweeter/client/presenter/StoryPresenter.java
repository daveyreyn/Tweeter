package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.ServicePageObserver;
import edu.byu.cs.tweeter.client.model.service.ServiceUserObserver;
import edu.byu.cs.tweeter.client.model.service.StoryService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

    public class StoryPresenter extends PagePresenter<Status> implements ServicePageObserver<Status>, ServiceUserObserver<Status> {


        public interface View extends PagePresenter.PagedView<Status> {

        }


        @Override
        public void getService(AuthToken authToken, User targetUser, int PAGE_SIZE, Status lastItem) {
            new StoryService().getStory(authToken, targetUser, PAGE_SIZE, lastItem, this);
        }

        @Override
        protected ServiceUserObserver getUserObserver() {
            return this;
        }

        @Override
        public ServicePageObserver getPageObserver() {
            return this;
        }

        public StoryPresenter(View view, AuthToken authToken, User targetUser){
            super(view, authToken, targetUser);
        }
}
