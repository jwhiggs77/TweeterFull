package edu.byu.cs.tweeter.client.model.service.handler;

import edu.byu.cs.tweeter.client.model.service.handler.observer.PagedNotificationObserver;

/**
 * Message handler (i.e., observer) for GetFollowingTask.
 */
public class GetFollowingHandler extends PagedNotificationHandler {
    public GetFollowingHandler(PagedNotificationObserver observer) {
        super(observer);
    }
}
