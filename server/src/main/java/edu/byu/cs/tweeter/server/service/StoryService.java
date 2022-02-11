package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.model.util.Pair;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.StoryDAO;

public class StoryService {
    private DAOFactory daoFactory;

    public StoryService(){}
    public StoryService(DAOFactory currFactory){
        daoFactory = currFactory;
    }

    public StoryResponse getStory(StoryRequest request){
        AuthTokenDAO authTokenDAO = getAuthTokenDAO();
        authTokenDAO.validateToken(request.getAuthToken().getAuthToken());

        Pair<List< Status >,Boolean> storyPair;

        if(request.getLastStatus() == null){
            storyPair = getStoryDAO().getStory(request.getUser().getAlias(), null, request.getLimit());
        }
        else {
            storyPair = getStoryDAO().getStory(request.getUser().getAlias(), request.getLastStatus().getDate(), request.getLimit());
        }
        StoryResponse response = new StoryResponse(storyPair.getFirst(), storyPair.getSecond());
        return response;

    }

    StoryDAO getStoryDAO(){
        return new StoryDAO();
    }

    public AuthTokenDAO getAuthTokenDAO() {
        return (AuthTokenDAO) daoFactory.getInstance().getAuthTokenDAO();
    }
}
