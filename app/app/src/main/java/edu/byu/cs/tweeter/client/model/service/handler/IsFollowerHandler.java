package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.handler.observer.IsFollowerObserver;

// IsFollowerHandler
public class IsFollowerHandler extends BackgroundTaskHandler<IsFollowerObserver> {
    public IsFollowerHandler(IsFollowerObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, IsFollowerObserver observer) {
        boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);
        observer.handleSuccess(isFollower);
    }
}
