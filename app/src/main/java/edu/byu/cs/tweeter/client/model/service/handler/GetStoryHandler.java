package edu.byu.cs.tweeter.client.model.service.handler;

import edu.byu.cs.tweeter.client.model.service.handler.observer.PagedNotificationObserver;

/**
 * Message handler (i.e., observer) for GetStoryTask.
 */
public class GetStoryHandler extends PagedNotificationHandler {
    public GetStoryHandler(PagedNotificationObserver observer) {
        super(observer);
    }
}
