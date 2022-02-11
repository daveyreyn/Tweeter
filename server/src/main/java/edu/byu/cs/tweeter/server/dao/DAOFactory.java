package edu.byu.cs.tweeter.server.dao;

abstract public class DAOFactory {

    private static DAOFactory instance;
    private UserDAOInterface userDAO;
    private AuthTokenDAOInterface authTokenDAO;
    private FollowDAOInterface followDAO;
    private FeedDAOInterface feedDAO;

    //private DAOFactory(){ }
    public static void setInstance(DAOFactory value) {
        instance = value;
    }
    public static DAOFactory getInstance(){
        return instance;
    }

    public UserDAOInterface getUserDAO(){
        return userDAO;
    }

    public AuthTokenDAOInterface getAuthTokenDAO() { return authTokenDAO; }

    public FollowDAOInterface getFollowDAO() { return followDAO; }

    public FeedDAOInterface getFeedDAO() { return feedDAO; }



}
