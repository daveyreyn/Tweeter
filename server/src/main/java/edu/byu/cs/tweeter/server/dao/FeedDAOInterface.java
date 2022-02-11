package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.BatchShareStatusRequest;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.util.Pair;

public interface FeedDAOInterface {
    Pair<List<Status>, Boolean> getFeed(String alias, int pageSize, String timestamp);
    void putFeed(Status status, List<User> followers);
    void batchShareStatus(Status status, List<User> followers);
}
