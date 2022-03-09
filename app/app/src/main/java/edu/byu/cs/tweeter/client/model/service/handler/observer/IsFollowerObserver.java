package edu.byu.cs.tweeter.client.model.service.handler.observer;

public interface IsFollowerObserver extends ServiceObserver {
    void handleSuccess(boolean isFollower);
}
