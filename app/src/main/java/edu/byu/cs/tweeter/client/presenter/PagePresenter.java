package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FeedService;
import edu.byu.cs.tweeter.client.model.service.ServiceUserObserver;
import edu.byu.cs.tweeter.client.model.service.ServicePageObserver;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagePresenter<T> extends Presenter implements ServicePageObserver<T>, ServiceUserObserver<T>  {

    public interface PagedView<U> extends PresenterView {
        void setLoading(boolean value);
        void addItems(List<U> items);
        void navigateToUser(User user);
    }

    private static final int PAGE_SIZE = 10;
    private PagedView view;
    private User targetUser;
    private AuthToken authToken;
    private T lastItem;

    @Override
    public void getPageSucceeded(List<T> items, boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
        this.isLoading = false;

        if(items.size() != 0){
            this.lastItem = items.get(items.size() - 1);
        }



        view.setLoading(false);

        view.addItems(items);
    }

    @Override
    public void handleFailure(String message) {
        view.displayErrorMessage(message);
    }

    @Override
    public void handleException(String exception) {

        view.displayErrorMessage(exception);
    }

    @Override
    public void handleSucceeded(String message) {

    }

    @Override
    public void GetUserSucceeded(T item) {
        view.navigateToUser((User) item);
    }

    private boolean hasMorePages = true;
    private boolean isLoading = false;

    public PagePresenter(PagedView view, AuthToken authToken, User targetUser) {
        super(view);
        this.view = view;
        this.authToken = authToken;
        this.targetUser = targetUser;
    }

    public void loadMoreItems(){
        if (!isLoading && hasMorePages) {
            isLoading = true;
            view.setLoading(true);
            getService(authToken, targetUser, PAGE_SIZE, lastItem);
        }
    }

    public abstract void getService(AuthToken authToken, User targetUser, int PAGE_SIZE, T lastItem);


    public void goToUser(String alias){
        ServiceUserObserver observer = getUserObserver();
        new UserService().getUser(Cache.getInstance().getCurrUserAuthToken(), alias,  observer);
        view.displayInfoMessage("Getting user's profile..." + alias);
    }

    protected abstract ServiceUserObserver getUserObserver();

    public abstract ServicePageObserver getPageObserver();

}

