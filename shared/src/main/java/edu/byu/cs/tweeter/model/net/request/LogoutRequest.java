package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LogoutRequest {
    private User user;
    private AuthToken authtoken;

    private LogoutRequest(){}

    public LogoutRequest(User user, AuthToken authToken){
        this.user = user;
        this.authtoken = authToken;
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AuthToken getAuthtoken() {
        return authtoken;
    }

    public void setAuthtoken(AuthToken authtoken) {
        this.authtoken = authtoken;
    }
}
