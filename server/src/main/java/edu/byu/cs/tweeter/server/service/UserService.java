package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetUserRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.GetUserResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAOInterface;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.FeedDAOInterface;
import edu.byu.cs.tweeter.server.dao.FollowDAOInterface;
import edu.byu.cs.tweeter.server.dao.S3DAO;
import edu.byu.cs.tweeter.server.dao.UserDAOInterface;
import edu.byu.cs.tweeter.model.util.Pair;
import java.util.UUID;
import java.sql.Timestamp;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserService {
    private DAOFactory daoFactory;


    public UserService(DAOFactory currFactory){
        daoFactory = currFactory;
    }
    public UserService(){

    }

    public LoginResponse login(LoginRequest request) {
        //System.out.println(request.getPassword());
        if(!request.getPassword().equals("1234")){
            request.setPassword(generateHash(request.getPassword()));
        }
        System.out.println(request.getPassword());
        Pair<User, String> loginInfo = getUserDAO().loginUser(request.getUsername());
        System.out.println(loginInfo.getSecond());
        if(loginInfo == null){
            throw new RuntimeException("[BadRequest] User not found");
        }
        if(!loginInfo.getSecond().equals(request.getPassword())){
            throw new RuntimeException("[BadRequest] incorrect");
        }
        LoginResponse response = new LoginResponse(loginInfo.getFirst(), generateAuthToken());

        return response;
    }

    public LogoutResponse logout(LogoutRequest request) {
        getAuthTokenDAO().deleteToken(request.getAuthtoken().getAuthToken());
        return new LogoutResponse(request.getUser(), request.getAuthtoken());
    }

    public RegisterResponse register(RegisterRequest request){
        S3DAO s3DAO = getS3DAO();
        String imageURL = null;
        try {
            imageURL = s3DAO.upload(request.getAlias(), request.getImageURL());
        }
        catch (Exception e){
            System.out.println(e);
        }

        String securePassword = generateHash(request.getPassword());

        User user = getUserDAO().registerUser(request.getAlias(),
                request.getFirstName(), request.getLastName(), securePassword, imageURL);

        if(user == null){
            return new RegisterResponse("User already exists");
        }

        RegisterResponse response = new RegisterResponse(user, generateAuthToken());
        return response;
    }

    public String generateHash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] bytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("501");
        }
    }

    public AuthToken generateAuthToken(){
        String token = UUID.randomUUID().toString();
        long curr_time = new Timestamp(System.currentTimeMillis()).getTime();
        getAuthTokenDAO().addToken(token, String.valueOf(curr_time));
        AuthToken authToken = new AuthToken(token, String.valueOf(curr_time));
        return authToken;

    }

    public IsFollowerResponse isFollower(IsFollowerRequest request){
        AuthTokenDAOInterface authTokenDAO = getAuthTokenDAO();
        authTokenDAO.validateToken(request.getAuthToken().getAuthToken());
        System.out.println(request.getFollower().getAlias() + "is follower alias in the userService");
        System.out.println(request.getFollowee().getAlias() + " is the followee alias in the userService");
        Boolean isFollowerInfo = getFollowDAO().checkFollowStatus(request.getFollower().getAlias(), request.getFollowee().getAlias());
        return new IsFollowerResponse(isFollowerInfo);
    }

    public GetUserResponse getUser(GetUserRequest request){
        AuthTokenDAOInterface authTokenDAO = getAuthTokenDAO();
        authTokenDAO.validateToken(request.getAuthToken().getAuthToken());
        System.out.println("timestamp: " + request.getAuthToken().getTimeStamp());
        User user = getUserDAO().getUser(request.getAlias());
        if(user == null){
            return new GetUserResponse("Unable to find user");
        }
        return new GetUserResponse(user);
    }

    public PostStatusResponse postStatus(PostStatusRequest request){
        AuthTokenDAOInterface authTokenDAO = getAuthTokenDAO();
        authTokenDAO.validateToken(request.getAuthToken().getAuthToken());

        UserDAOInterface userDAO = getUserDAO();

        String statusInfo = userDAO.postStatus(request.getStatus().getUser().getAlias(),
                request.getStatus().getUser().getFirstName(), request.getStatus().getUser().getLastName(),
                request.getStatus().getUser().getImageUrl(), request.getStatus().getPost(),
                request.getStatus().getDate(), request.getStatus().getUrls(), request.getStatus().getMentions());
        if(statusInfo != null){
            return new PostStatusResponse(statusInfo);
        }
        addToFeed(request);
        return new PostStatusResponse();
    }

    public void addToFeed(PostStatusRequest request) {
        List<User> followers = getFollowDAO().getFollowers(request.getStatus().getUser().getAlias(), null, 10).getFirst();
        getFeedDAO().putFeed(request.getStatus(), followers);
    }

    public UserDAOInterface getUserDAO(){
        return daoFactory.getInstance().getUserDAO();
    }

    public FollowDAOInterface getFollowDAO(){
        return daoFactory.getInstance().getFollowDAO();
    }

    public FeedDAOInterface getFeedDAO() { return daoFactory.getInstance().getFeedDAO(); }

    public AuthTokenDAOInterface getAuthTokenDAO() {
        return daoFactory.getInstance().getAuthTokenDAO();
    }

    public S3DAO getS3DAO(){
        return new S3DAO();
    }
}
