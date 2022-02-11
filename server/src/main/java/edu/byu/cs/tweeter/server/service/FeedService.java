package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.BatchShareStatusRequest;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.util.Pair;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAOInterface;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAOInterface;

public class FeedService  {
    private DAOFactory daoFactory;

    public FeedService(DAOFactory currFactory){
        daoFactory = currFactory;
    }
    public FeedService(){}

    public FeedResponse getFeed(FeedRequest request){
        AuthTokenDAOInterface authTokenDAO = getAuthTokenDAO();
        authTokenDAO.validateToken(request.getAuthToken().getAuthToken());

        Pair<List<Status>,Boolean> feedPair;
        if(request.getLastStatus() == null){
            feedPair = getFeedDAO().getFeed(request.getUser().getAlias(), request.getLimit(), null);
        }
        else {
            feedPair = getFeedDAO().getFeed(request.getUser().getAlias(), request.getLimit(), request.getLastStatus().getDate());
        }

        FeedResponse response = new FeedResponse(feedPair.getFirst(), feedPair.getSecond());
        return response;
    }

    FeedDAOInterface getFeedDAO(){
        return daoFactory.getInstance().getFeedDAO();
    }
    public AuthTokenDAOInterface getAuthTokenDAO() {
        return daoFactory.getInstance().getAuthTokenDAO();
    }

    public void batchFeedUpdate(BatchShareStatusRequest request){
        AuthTokenDAOInterface authTokenDAO = getAuthTokenDAO();
        authTokenDAO.validateToken(request.getAuthToken().getAuthToken());
        getFeedDAO().batchShareStatus(request.getStatus(), request.getFollowers());
    }
}
