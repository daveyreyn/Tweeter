package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.List;
import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;

public class FollowService {


    public void getFollowing(AuthToken authToken,
                             User targetUser, int limit, User lastFollowee, ServicePageObserver observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(authToken, targetUser,
                limit, lastFollowee, new GetFollowingHandler(observer));
        ExecuteService executeService = new ExecuteService();
        executeService.execute(getFollowingTask);
    }

    private class GetFollowingHandler extends BackgroundTaskHandler<ServicePageObserver>{
        public GetFollowingHandler(ServicePageObserver observer) {
            super(observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return null;
        }

        @Override
        protected void handleSuccessMessage(ServicePageObserver observer, Bundle data) {
            List<User> followees = (List<User>) data.getSerializable(GetFollowingTask.ITEMS_KEY);
            boolean hasMorePages = data.getBoolean(GetFollowingTask.MORE_PAGES_KEY);
            observer.getPageSucceeded(followees, hasMorePages);
        }
    }


    public void getFollowers(AuthToken authToken,
                             User targetUser, int limit, User lastFollower, ServicePageObserver observer){
        GetFollowersTask getFollowersTask = new GetFollowersTask(authToken,
                targetUser, limit, lastFollower, new GetFollowersHandler(observer));
        ExecuteService executeService = new ExecuteService();
        executeService.execute(getFollowersTask);
    }

    public void GetFollowersCount(AuthToken authToken, User user, ServiceUserObserver observer){
        GetFollowersCountTask getFollowersCountTask = new GetFollowersCountTask(authToken,
                user, new GetFollowersCountHandler(observer));
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            ExecuteService executeService = new ExecuteService();
            executeService.execute(getFollowersCountTask);
        }, 0);

    }

    public void GetFollowingCount(AuthToken authToken, User user, ServiceUserObserver observer){
        GetFollowingCountTask getFollowingCountTask = new GetFollowingCountTask(authToken,
                user, new GetFollowingCountHandler(observer));
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            ExecuteService executeService = new ExecuteService();
            executeService.execute(getFollowingCountTask);
        }, 0);

    }


    private class GetFollowersHandler extends BackgroundTaskHandler<ServicePageObserver> {

        public GetFollowersHandler(ServicePageObserver observer){
            super(observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return null;
        }

        @Override
        protected void handleSuccessMessage(ServicePageObserver observer, Bundle data) {
            List<User> followers = (List<User>) data.getSerializable(GetFollowersTask.ITEMS_KEY);
            boolean hasMorePages = data.getBoolean(GetFollowersTask.MORE_PAGES_KEY);
            observer.getPageSucceeded(followers, hasMorePages);
        }
    }

    private class GetFollowersCountHandler extends BackgroundTaskHandler<ServiceUserObserver> {

        public GetFollowersCountHandler(ServiceUserObserver observer){
            super(observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return null;
        }

        @Override
        protected void handleSuccessMessage(ServiceUserObserver observer, Bundle data) {
            int count = data.getInt(GetFollowersCountTask.COUNT_KEY);
            observer.GetUserSucceeded(count);
        }
    }

    private class GetFollowingCountHandler extends BackgroundTaskHandler<ServiceUserObserver> {

        public GetFollowingCountHandler(ServiceUserObserver observer){
            super(observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return null;
        }

        @Override
        protected void handleSuccessMessage(ServiceUserObserver observer, Bundle data) {
            int count = data.getInt(GetFollowingCountTask.COUNT_KEY);
            observer.GetUserSucceeded(count);
        }
    }



    public void isFollower(AuthToken authToken,
                           User user, User selectedUser,  ServiceObserver observer){
        IsFollowerTask isFollowerTask = new IsFollowerTask(authToken, user, selectedUser, new IsFollowerHandler(observer));
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
        ExecuteService executeService = new ExecuteService();
        executeService.execute(isFollowerTask);
        }, 0);
    }

    private class IsFollowerHandler extends BackgroundTaskHandler<ServiceObserver> {

        public IsFollowerHandler(ServiceObserver observer){
            super(observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return null;
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Bundle data) {
            boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
            System.err.println("is follower in followservice " + isFollower);
            if(isFollower){
                observer.handleSucceeded("isFollower");
            }
            else {
                observer.handleSucceeded("notFollower");
            }

        }
    }


    public void follow(AuthToken authToken,
                           User user, User selectedUser, boolean follow, ServiceObserver observer){
        FollowTask followTask = new FollowTask(authToken, selectedUser, new FollowHandler(observer));

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            ExecuteService executeService = new ExecuteService();
            executeService.execute(followTask);
        }, 0);

    }


    private class FollowHandler extends BackgroundTaskHandler<ServiceObserver>  {


        public FollowHandler(ServiceObserver observer){
            super(observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return null;
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Bundle data) {
            observer.handleSucceeded("follow");
        }
    }



    public void unfollow(AuthToken authToken,
                         User user, User selectedUser, boolean follow, ServiceObserver observer){
        UnfollowTask unfollowTask = new UnfollowTask(authToken, selectedUser, new UnfollowHandler(observer));

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            ExecuteService executeService = new ExecuteService();
            executeService.execute(unfollowTask);
        }, 0);
    }

    private class UnfollowHandler extends BackgroundTaskHandler<ServiceObserver>  {


        public UnfollowHandler(ServiceObserver observer){
            super(observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return null;
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Bundle data) {
            observer.handleSucceeded("unfollow");
        }
    }





}