package edu.byu.cs.tweeter.client.backgroundTask;


import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends AuthorizedTask {

    private PostStatusRequest request;

    /**
     * The new status being sent. Contains all properties of the status,
     * including the identity of the user sending the status.
     */
    private final Status status;

    public PostStatusTask(AuthToken authToken, Status status, Handler messageHandler) {
        super(authToken, messageHandler);
        this.status = status;
        request = new PostStatusRequest(status, authToken);
    }

    @Override
    protected void runTask() {
        PostStatusResponse response = null;
        try {
            response = getServerFacade().postStatus(request, "/poststatus");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        // We could do this from the presenter, without a task and handler, but we will
        // eventually access the database from here when we aren't using dummy data.
    }

    @Override
    protected void loadBundle(Bundle msgBundle) {
        // Nothing to load
    }
}
