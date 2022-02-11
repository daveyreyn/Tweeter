package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.model.util.Pair;

public interface StoryDAOInterface {

    Pair<List<Status>, Boolean> getStory(String alias, String timeStamp, int pageSize);
}
