package edu.byu.cs.tweeter.client.model.service.handler;

import edu.byu.cs.tweeter.client.model.service.handler.observer.PagedNotificationObserver;

/**
 * Message handler (i.e., observer) for GetFeedTask.
 */
public class GetFeedHandler extends PagedNotificationHandler {
    public GetFeedHandler(PagedNotificationObserver observer) {
        super(observer);
    }
}
