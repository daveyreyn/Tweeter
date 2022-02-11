package edu.byu.cs.tweeter.client.presenter;

import android.content.Intent;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.ServicePageObserver;
import edu.byu.cs.tweeter.client.model.service.ServiceUserObserver;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagePresenter<User>  implements ServicePageObserver<User>, ServiceUserObserver<User>{



    public interface View extends PagePresenter.PagedView<Status> {

    }


    @Override
    public void getService(AuthToken authToken, User targetUser, int PAGE_SIZE, User lastItem) {
        new FollowService().getFollowing(authToken, targetUser, PAGE_SIZE, lastItem, this);

    }

    @Override
    protected ServiceUserObserver getUserObserver() {
        return this;
    }

    @Override
    public ServicePageObserver getPageObserver() {
        return this;
    }

    public FollowingPresenter(View view, AuthToken authToken, User targetUser){
        super(view, authToken, targetUser);
    }

}
