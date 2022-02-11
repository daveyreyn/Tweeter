package edu.byu.cs.tweeter.client.backgroundTask;


import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.util.Pair;
//import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that creates a new user account and logs in the new user (i.e., starts a session).
 */
public class RegisterTask extends AuthenticationTask {

    /**
     * The user's first name.
     */
    private final String firstName;

    /**
     * The user's last name.
     */
    private final String lastName;

    /**
     * The base-64 encoded bytes of the user's profile image.
     */
    private final String image;

    //private final byte[] imageBytes;

    private RegisterRequest request;

    public RegisterTask(String firstName, String lastName, String username, String password,
                        String image, Handler messageHandler) {
        super(messageHandler, username, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.image = image;
        request = new RegisterRequest(firstName, lastName, username, password, image);
    }

    @Override
    protected Pair<User, AuthToken> runAuthenticationTask() {

        RegisterResponse response = null;
        try {
            response = getServerFacade().register(request, "/register");
        }
        catch (Exception e){
            System.out.println(e);
        }
        User registeredUser = response.getUser();
        AuthToken authToken = response.getAuthToken();
        return new Pair<>(registeredUser, authToken);
    }
}
