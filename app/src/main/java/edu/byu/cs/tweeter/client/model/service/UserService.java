package edu.byu.cs.tweeter.client.model.service;

import android.os.Bundle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService {


    public UserService(){

    }

    public void getUser(AuthToken authToken, String alias, ServiceUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(authToken, alias, new GetUserHandler(observer));
        ExecuteService executeService = new ExecuteService();
        executeService.execute(getUserTask);
    }

    private class GetUserHandler extends BackgroundTaskHandler<ServiceUserObserver> {

        public GetUserHandler(ServiceUserObserver observer){
            super(observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return null;
        }

        @Override
        protected void handleSuccessMessage(ServiceUserObserver observer, Bundle data) {
            User user = (User) data.getSerializable(GetUserTask.USER_KEY);
            observer.GetUserSucceeded(user);

        }
    }

    public void register(String firstName, String lastName, String alias, String password,
                         String image, ServiceUserObserver observer){
        RegisterTask registerTask = new RegisterTask(firstName, lastName, alias, password, image,  new RegisterHandler(observer));
        ExecuteService executeService = new ExecuteService();
        executeService.execute(registerTask);
    }

    private class RegisterHandler extends BackgroundTaskHandler<ServiceUserObserver> {

        public RegisterHandler(ServiceUserObserver observer){
            super(observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return null;
        }

        @Override
        protected void handleSuccessMessage(ServiceUserObserver observer, Bundle data) {
            User registeredUser = (User) data.getSerializable(RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) data.getSerializable(RegisterTask.AUTH_TOKEN_KEY);
            observer.GetUserSucceeded(registeredUser);
            Cache.getInstance().setCurrUser(registeredUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);
        }

    }


    public void login(String alias, String password, ServiceUserObserver observer){
        LoginTask loginTask = new LoginTask(alias, password, new LoginHandler(observer));
        ExecuteService executeService = new ExecuteService();
        executeService.execute(loginTask);

    }

    private class LoginHandler extends BackgroundTaskHandler<ServiceUserObserver> {

        public LoginHandler(ServiceUserObserver observer){
            super(observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return null;
        }

        @Override
        protected void handleSuccessMessage(ServiceUserObserver observer, Bundle data) {
            User loggedInUser = (User) data.getSerializable(RegisterTask.USER_KEY);
            AuthToken authToken = (AuthToken) data.getSerializable(RegisterTask.AUTH_TOKEN_KEY);
            observer.GetUserSucceeded(loggedInUser);
            Cache.getInstance().setCurrUser(loggedInUser);
            Cache.getInstance().setCurrUserAuthToken(authToken);
        }
    }



    public void getPostStatus(AuthToken authToken, Status status, ServiceUserObserver observer){

        PostStatusTask postStatusTask = new PostStatusTask(authToken, status, new PostStatusHandler(observer));

        ExecuteService executeService = new ExecuteService();
        executeService.execute(postStatusTask);
    }


    private class PostStatusHandler extends BackgroundTaskHandler<ServiceUserObserver>  {

        public PostStatusHandler(ServiceUserObserver observer){
            super(observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return "Post Status Failed";
        }


        @Override
        protected void handleSuccessMessage(ServiceUserObserver observer, Bundle data) {
            String message = "Successfully Posted!";
            observer.handleSucceeded(message);

        }
    }



    public void logout(AuthToken authToken, ServiceObserver observer){
        LogoutTask logoutTask = new LogoutTask(authToken, new LogoutHandler(observer));

        ExecuteService executeService = new ExecuteService();
        executeService.execute(logoutTask);
    }
    private class LogoutHandler extends BackgroundTaskHandler<ServiceObserver> {

        public LogoutHandler(ServiceObserver observer){
            super(observer);
        }

        @Override
        protected String getFailedMessagePrefix() {
            return null;
        }

        @Override
        protected void handleSuccessMessage(ServiceObserver observer, Bundle data) {
            String message = "Logout Successful";
            observer.handleSucceeded(message);
        }
    }
}
