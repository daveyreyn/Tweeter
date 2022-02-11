package edu.byu.cs.tweeter.client.model.service;

public interface ServiceUserObserver<T> extends ServiceObserver {
    void GetUserSucceeded(T item);
}
