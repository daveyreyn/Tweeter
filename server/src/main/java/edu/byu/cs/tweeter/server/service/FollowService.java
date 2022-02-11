package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowUserRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowUserRequest;
import edu.byu.cs.tweeter.model.net.response.FollowUserResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowUserResponse;
import edu.byu.cs.tweeter.model.util.Pair;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAOInterface;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.FeedDAOInterface;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAOInterface;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.UserDAOInterface;


public class FollowService {

    private DAOFactory daoFactory;

    public FollowService(){}

    public FollowService(DAOFactory currFactory){
        daoFactory = currFactory;
    }

    public FollowUserResponse followUser(FollowUserRequest request){
        AuthTokenDAOInterface authTokenDAO = getAuthTokenDAO();
        authTokenDAO.validateToken(request.getAuthToken().getAuthToken());
        FollowDAOInterface followDAO = getFollowDAO();

        String followUserInfo = followDAO.followUser(request.getFollower().getAlias(), request.getFollowee().getAlias(),
                request.getFollower().getFirstName(), request.getFollower().getLastName(),
                request.getFollower().getImageUrl(), request.getFollowee().getFirstName(),
                request.getFollowee().getLastName(), request.getFollowee().getImageUrl());

        if(followUserInfo != null){
            return new FollowUserResponse("error following user");
        }

        updateFollowers(request.getFollowee().getAlias(), true);
        updateFollowing(request.getFollower().getAlias(), true);
        //updateFeed(request.getFollower().getAlias(), request.getFollowee().getAlias());
        return new FollowUserResponse();
    }

    public UnfollowUserResponse unfollowUser(UnfollowUserRequest request){
        AuthTokenDAOInterface authTokenDAO = getAuthTokenDAO();
        authTokenDAO.validateToken(request.getAuthToken().getAuthToken());
        FollowDAOInterface followDAO = getFollowDAO();
        String unfollowUserInfo = followDAO.unfollowUser(request.getFollower().getAlias(), request.getFollowee().getAlias());
        if(unfollowUserInfo != null){
            return new UnfollowUserResponse("error unfollowing user");
        }
        updateFollowers(request.getFollowee().getAlias(), false);
        updateFollowing(request.getFollower().getAlias(), false);
        return new UnfollowUserResponse();
    }

    public void updateFollowing(String alias, boolean follow){
        getUserDAO().updateFollowing(alias, follow);
    }
    public void updateFollowers(String alias, boolean follow){
        getUserDAO().updateFollowers(alias, follow);
    }



    public FollowingResponse getFollowees(FollowingRequest request) {
        AuthTokenDAOInterface authTokenDAO = getAuthTokenDAO();
        authTokenDAO.validateToken(request.getAuthToken().getAuthToken());
        Pair<List<User>, Boolean> getFolloweesInfo = getFollowDAO().getFollowees(request.getFollowerAlias(), request.getLastFolloweeAlias(), request.getLimit());

        return new FollowingResponse(getFolloweesInfo.getFirst(), getFolloweesInfo.getSecond());
    }

    public FollowerResponse getFollowers(FollowerRequest request){
        AuthTokenDAOInterface authTokenDAO = getAuthTokenDAO();
        authTokenDAO.validateToken(request.getAuthToken().getAuthToken());
        Pair<List<User>, Boolean> getFollowersInfo = getFollowDAO().getFollowers(request.getFolloweeAlias(), request.getLastFollowerAlias(), request.getLimit());
        return new FollowerResponse(getFollowersInfo.getFirst(), getFollowersInfo.getSecond());
    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request){
        System.out.println(request.toString());
        int numFollowing = getUserDAO().getNumFollowing(request.getUserAlias());
        FollowingCountResponse response = new FollowingCountResponse();
        response.setNumFollowing(numFollowing);
        return response;
    }


    public FollowerCountResponse getFollowerCount(FollowerCountRequest request){
        System.out.println(request.toString());
        int numFollowers = getUserDAO().getNumFollowers(request.getUserAlias());
        FollowerCountResponse response = new FollowerCountResponse();
        response.setNumFollower(numFollowers);
        return response;
    }


    public FollowDAOInterface getFollowDAO() {
        return daoFactory.getInstance().getFollowDAO();
    }

    public UserDAOInterface getUserDAO() { return  daoFactory.getInstance().getUserDAO(); }



    public AuthTokenDAOInterface getAuthTokenDAO() {
        return  daoFactory.getInstance().getAuthTokenDAO();
    }

    public FeedDAOInterface getFeedDAO(){
        return daoFactory.getInstance().getFeedDAO();
    }
}
