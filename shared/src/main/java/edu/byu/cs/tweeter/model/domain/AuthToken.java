package edu.byu.cs.tweeter.model.domain;

import java.io.Serializable;

/**
 * Represents an auth token in the system.
 */
public class AuthToken implements Serializable {

    private String authToken;
    private String timestamp;


    public AuthToken(){

    }

    public AuthToken(String authTokenString, String timestamp) {
        this.authToken = authTokenString;
        this.timestamp = timestamp;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getTimeStamp() {
        return timestamp;
    }

    public void setAuthToken(String authToken){
        this.authToken = authToken;
    }

    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }


}
