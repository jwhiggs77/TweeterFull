package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.handler.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

import java.util.List;

public abstract class PagedPresenter<T> extends Presenter {

    public interface View<T> extends Presenter.View {
        void setLoadingStatus(boolean value);

        void addItemList(List<T> items);

        void startActivity(User user);
    }

    private static final int PAGE_SIZE = 10;
    protected T lastItem;
    protected boolean hasMorePages;
    protected boolean isLoading = false;

    private View view;
    private UserService userService;

    public PagedPresenter(View view) {
        super(view);
        this.view = view;
        userService = new UserService();
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            setLoading(true);
            view.setLoadingStatus(isLoading());
            getItems(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastItem);
        }
    }

    public abstract void getItems(AuthToken authToken, User user, int pageSize, T lastItem);

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public abstract class GetListObserver extends Observer implements PagedNotificationObserver<T> {

        @Override
        public void handleSuccess(List<T> itemList, boolean hasMorePages) {
            lastItem = (itemList.size() > 0) ? itemList.get(itemList.size() - 1) : null;
            setHasMorePages(hasMorePages);
            setLoading(false);
            view.setLoadingStatus(isLoading());
            view.addItemList(itemList);
        }

        @Override
        public void handleMessage(String message) {
            setLoading(false);
            view.setLoadingStatus(isLoading());
            view.displayMessage(getMessageTag() + message);
        }

        @Override
        public void handleException(Exception exception) {
            setLoading(false);
            view.setLoadingStatus(isLoading());
            view.displayMessage(getMessageTag() + exception.getMessage());
        }
    }

    public class GetUserObserver extends Observer implements edu.byu.cs.tweeter.client.model.service.handler.observer.GetUserObserver {

        @Override
        public void handleSuccess(User user) {
            view.startActivity(user);
        }

        @Override
        public String getMessageTag() {
            return "Failed to get user's profile: ";
        }
    }

    public void getUser(String userAlias) {
        userService.getUser(Cache.getInstance().getCurrUserAuthToken(), userAlias, new GetUserObserver());
    }
}
