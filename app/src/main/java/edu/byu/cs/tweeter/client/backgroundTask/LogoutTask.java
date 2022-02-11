package edu.byu.cs.tweeter.client.backgroundTask;


import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthorizedTask {
    private LogoutRequest request;

    public LogoutTask(AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
        User currUser = Cache.getInstance().getCurrUser();
        request = new LogoutRequest(currUser, authToken);
    }

    @Override
    protected void runTask() {
        LogoutResponse response = null;
        try {
            response = getServerFacade().logout(request, "/logout");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    @Override
    protected void loadBundle(Bundle msgBundle) {
        // Nothing to load
    }
}
