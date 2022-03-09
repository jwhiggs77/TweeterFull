package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PagedTask;
import edu.byu.cs.tweeter.client.model.service.handler.observer.PagedNotificationObserver;

import java.util.List;

public class PagedNotificationHandler<T> extends BackgroundTaskHandler<PagedNotificationObserver> {
    public PagedNotificationHandler(PagedNotificationObserver<T> observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, PagedNotificationObserver observer) {
        List<T> items = (List<T>) data.getSerializable(PagedTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(PagedTask.MORE_PAGES_KEY);
        observer.handleSuccess(items, hasMorePages);
    }
}
