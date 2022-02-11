package edu.byu.cs.tweeter.server.dao;

public class DynamoDAOFactory extends DAOFactory {

    @Override
    public UserDAOInterface getUserDAO(){
        return new UserDAO();
    }

    @Override
    public AuthTokenDAOInterface getAuthTokenDAO(){
        return new AuthTokenDAO();
    }

    @Override
    public FollowDAOInterface getFollowDAO() { return new FollowDAO(); }

    @Override
    public FeedDAOInterface getFeedDAO() { return new FeedDAO(); }
}
