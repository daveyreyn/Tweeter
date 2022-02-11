package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;

public abstract class AuthorizedTask extends BackgroundTask {

    /**
     * Auth token for logged-in user.
     * This user is the "follower" in the relationship.
     */
    private final AuthToken authToken;

    private ServerFacade serverFacade;

    protected AuthorizedTask(AuthToken authToken, Handler messageHandler) {
        super(messageHandler);
        this.authToken = authToken;
    }

    public ServerFacade getServerFacade() {
        if(serverFacade == null) {
            serverFacade = new ServerFacade();
        }

        return serverFacade;
    }
}