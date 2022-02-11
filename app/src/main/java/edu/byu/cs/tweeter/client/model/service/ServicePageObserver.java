package edu.byu.cs.tweeter.client.model.service;

import java.util.List;

public interface ServicePageObserver<T> extends  ServiceObserver {
    void getPageSucceeded(List<T> items, boolean hasMorePages);
}
