package edu.byu.cs.tweeter.client.model.service.handler.observer;

public interface GetCountObserver extends ServiceObserver {
    void handleSuccess(int count);
}
