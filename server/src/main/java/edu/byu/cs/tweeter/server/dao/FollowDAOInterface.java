package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowUserRequest;
import edu.byu.cs.tweeter.model.net.response.FollowUserResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowUserResponse;
import edu.byu.cs.tweeter.model.util.Pair;

public interface FollowDAOInterface {

    String followUser(String followerAlias, String followeeAlias, String followerFirstName,
                                  String followerLastName, String followerImageURL, String followeeFirstName,
                                  String followeeLastName, String followeeImageUR);

    String unfollowUser(String followerAlias, String followeeAlias);
    Pair<List<User>, Boolean> getFollowees(String user, String lastFolloweeAlias, int pageSize);
    Pair<List<User>, Boolean>  getFollowers(String followeeAlias, String lastFollowerAlias, int pageSize);
    Boolean checkFollowStatus(String followerAlias, String followeeAlias);
    void addFollowBatch(List<Pair<User, User>> pairList);
}
