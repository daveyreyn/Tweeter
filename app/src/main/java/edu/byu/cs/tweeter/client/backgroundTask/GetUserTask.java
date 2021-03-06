package edu.byu.cs.tweeter.client.backgroundTask;


import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;


/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends AuthorizedTask {

    private GetUserRequest request;

    public static final String USER_KEY = "user";

    /**
     * Alias (or handle) for user whose profile is being retrieved.
     */
    private final String alias;

    private User user;

    public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
        super(authToken, messageHandler);
        this.alias = alias;
        request = new GetUserRequest(alias, authToken);
    }

    @Override
    protected void runTask() {
        GetUserResponse response = null;
        try {
            response = getServerFacade().getUser(request, "/getuser");
            user = response.getUser();
            BackgroundTaskUtils.loadImage(user);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }

        //user = getUser();



    }

    @Override
    protected void loadBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, user);
    }


}
