package edu.byu.cs.tweeter.client.model.service;

public interface ServiceObserver {
    void handleSucceeded(String message);
    void handleFailure(String message);
    void handleException(String exception);
}
