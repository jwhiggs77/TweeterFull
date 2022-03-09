package edu.byu.cs.tweeter.client.model.service.handler;

import edu.byu.cs.tweeter.client.model.service.handler.observer.PagedNotificationObserver;

/**
 * Message handler (i.e., observer) for GetFollowersTask.
 */
public class GetFollowersHandler extends PagedNotificationHandler {
    public GetFollowersHandler(PagedNotificationObserver observer) {
        super(observer);
    }
}
