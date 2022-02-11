package edu.byu.cs.tweeter.client.presenter;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.ServicePageObserver;
import edu.byu.cs.tweeter.client.model.service.ServiceUserObserver;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainActivityPresenter extends Presenter implements ServiceUserObserver, ServicePageObserver {


    @Override
    public void getPageSucceeded(List items, boolean hasMorePages) {

    }

    @Override
    public void GetUserSucceeded(Object item) {
        //check if item is of type status then send that status over
    }

    private class FollowerCountObserver implements ServiceUserObserver{

        @Override
        public void GetUserSucceeded(Object item) {
            view.displayFollowerCount((Integer) item);
        }

        @Override
        public void handleSucceeded(String message) {

        }

        @Override
        public void handleFailure(String message) {

        }

        @Override
        public void handleException(String exception) {

        }
    }

    private class FollowingCountObserver implements ServiceUserObserver{

        @Override
        public void GetUserSucceeded(Object item) {
            view.displayFollowingCount((Integer) item);
        }

        @Override
        public void handleSucceeded(String message) {

        }

        @Override
        public void handleFailure(String message) {

        }

        @Override
        public void handleException(String exception) {

        }
    }

    @Override
    public void handleSucceeded(String message) {

        if(message.equals("isFollower") ){
            view.displayFollowButton(true);
        }
        else if (message.equals("notFollower") ){
            view.displayFollowButton(false);
        }
        else if (message.equals("follow") ){
            view.followUnfollow(true, "");
        }
        else if (message.equals("unfollow") ){
            view.followUnfollow(false, "");
        }

        else if (message.equals("Successfully Posted!")){
            view.displaySuccessMessage(message);
        }

    }

    @Override
    public void handleFailure(String message) {
        view.displayErrorMessage(message);
    }

    @Override
    public void handleException(String exception) {
        view.displayErrorMessage(exception);
    }

    public interface View extends PresenterView {
        void displaySuccessMessage(String message);
        void followUnfollow(boolean isFollowing, String message);
        void displayFollowerCount(int count);
        void displayFollowingCount(int count);
        void displayFollowButton(boolean isFollower);
    }

    private View view;
    private User targetUser;
    private AuthToken authToken;

    private UserService userService;
    private Status status;

    public MainActivityPresenter(View view, AuthToken authToken, User targetUser){
        super(view);
        this.view = view;
        this.authToken = authToken;
        this.targetUser = targetUser;

    }

    public void formatStatus(String stringStatus) throws MalformedURLException, ParseException {
        Status newStatus = new Status(stringStatus, targetUser, getFormattedDateTime(), parseURLs(stringStatus), parseMentions(stringStatus));
        postStatus(newStatus);

    }


    private List<String> parseURLs(String post) throws MalformedURLException {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    private List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    private String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    private int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    //for testing
    public MainActivityPresenter(View view, AuthToken authToken, User targetUser, UserService service){
        super(view);
        this.view = view;
        this.authToken = authToken;
        this.targetUser = targetUser;
        this.userService = service;
    }

    public void followersCount(){
        new FollowService().GetFollowersCount(authToken, targetUser, new FollowerCountObserver());
    }

    public void followingCount(){
        new FollowService().GetFollowingCount(authToken, targetUser, new FollowingCountObserver());


    }


    public void isFollowing(User selectedUser){
        new FollowService().isFollower(authToken, Cache.getInstance().getCurrUser(), selectedUser, this);

    }


    public void followUnfollow(User selectedUser, boolean follow){

        if(follow){
            new FollowService().follow(authToken, targetUser, selectedUser, follow, this);
        }
        else{
            new FollowService().unfollow(authToken, targetUser, selectedUser, follow, this);
        }
    }


    public void postStatus(Status status){
        view.displayInfoMessage("Posting Status...");
        if(userService != null){
            userService.getPostStatus(authToken, status, this);
            this.status = status;
        }
        else {
            userService = new UserService();

            userService.getPostStatus(authToken, status, this);
        }
    }

    //for test
    public UserService getUserService(){
        return userService;
    }

    //for test
    public AuthToken getAuthToken(){
        return authToken;
    }

    //for test
    public User getTargetUser(){
        return targetUser;
    }

    //for test
    public View getView() {
        return view;
    }

    //for test
    public Status getStatus(){
        return status;
    }

    public void logout(AuthToken authToken){
        new UserService().logout(authToken, this);
    }


}
