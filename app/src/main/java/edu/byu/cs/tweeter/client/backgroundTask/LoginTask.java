package edu.byu.cs.tweeter.client.backgroundTask;


import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.util.Pair;
//import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticationTask {

    private LoginRequest request;

    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler, username, password);

        request = new LoginRequest(username, password);
    }

    @Override
    protected Pair<User, AuthToken> runAuthenticationTask() throws IOException, TweeterRemoteException {

        LoginResponse response = null;

        response = getServerFacade().login(request, "/login");


        User loggedInUser = response.getUser();
        AuthToken authToken = response.getAuthToken();
        return new Pair<>(loggedInUser, authToken);
    }
}
