package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.util.Pair;

public interface UserDAOInterface {

    User getUser(String alias);

    User registerUser(String alias, String firstName, String lastName,
                                  String password, String imageURL);

    Pair<User, String> loginUser(String alias);


    String postStatus(String alias, String firstName, String lastName, String imageURL, String content, String timestamp, List<String>urls, List<String> mentions);

    int getNumFollowing(String alias);
    int getNumFollowers(String alias);
    void updateFollowers(String alias, boolean follow);
    void updateFollowing(String alias, boolean follow);
    void addUserBatch(List<User> users);
    void updateBatchFollowers(String alias, int numFollowers);
    void resetFollowers(String alias);

}
