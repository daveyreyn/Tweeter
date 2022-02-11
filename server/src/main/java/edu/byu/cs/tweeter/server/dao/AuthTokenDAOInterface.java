package edu.byu.cs.tweeter.server.dao;

public interface AuthTokenDAOInterface {

    void addToken(String token, String timestamp);

    String validateToken(String token);

    void deleteToken(String token);
}
