package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FeedService;
import edu.byu.cs.tweeter.client.model.service.ServicePageObserver;
import edu.byu.cs.tweeter.client.model.service.ServiceUserObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagePresenter<Status> implements ServicePageObserver<Status>, ServiceUserObserver<Status>{

    public interface View extends PagedView<Status> {

    }

    @Override
    public void getService(AuthToken authToken, User targetUser, int PAGE_SIZE, Status lastItem) {
        new FeedService().GetFeed(authToken, targetUser, PAGE_SIZE, lastItem, this);

    }

    @Override
    public ServicePageObserver getPageObserver() {
        return this;
    }

    @Override
    public ServiceUserObserver getUserObserver() { return this; }

    public FeedPresenter(View view, AuthToken authToken, User targetUser){
        super(view, authToken, targetUser);
    }




}