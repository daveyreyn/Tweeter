package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.ServicePageObserver;
import edu.byu.cs.tweeter.client.model.service.ServiceUserObserver;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagePresenter<User>  implements ServicePageObserver<User>, ServiceUserObserver<User> {


    public interface View extends PagePresenter.PagedView {
    }


    public FollowersPresenter(View view, AuthToken authToken, User targetUser){
        super(view, authToken, targetUser);
    }

    @Override
    public void getService(AuthToken authToken, User targetUser, int PAGE_SIZE, User lastItem) {
        new FollowService().getFollowers(authToken, targetUser, PAGE_SIZE, lastItem,  this);
    }

    @Override
    protected ServiceUserObserver getUserObserver() {
        return this;
    }

    @Override
    public ServicePageObserver getPageObserver() {
        return this;
    }


}
