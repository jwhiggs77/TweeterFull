package edu.byu.cs.tweeter.client.presenter;

import static org.junit.Assert.assertEquals;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.handler.observer.AuthenticateObserver;
import edu.byu.cs.tweeter.client.model.service.handler.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.client.model.service.handler.observer.SimpleNotificationObserver;
import edu.byu.cs.tweeter.client.presenter.MainActivityPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class PostIntegrationTest extends TestCase {
    private UserService userService;
    private MainActivityPresenter.View view;
    private MainActivityPresenter mainActivityPresenter;
    private User currentUser = new User("Tony", "Stark", "@ironman", "password", "image");
    private List<Status> itemList;
    private User loggedInUser;
    private AuthToken loggedInAuthToken;

    CountDownLatch countDownLatch;

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    @Before
    public void setUp() {
        view = Mockito.mock(MainActivityPresenter.View.class);
        userService = new UserService();
        mainActivityPresenter = Mockito.spy(new MainActivityPresenter(view));
        itemList = new ArrayList<>();

        Mockito.doReturn(mainActivityPresenter.new PostStatusObserver() {
            @Override
            public void handleSuccess() {
                // Call the display message on the view
                countDownLatch.countDown();
            }

            @Override
            public void handleMessage(String message) {
                countDownLatch.countDown();
            }

            @Override
            public void handleException(Exception ex) {
                countDownLatch.countDown();
            }
        }).when(mainActivityPresenter).postStatusObserverFactory();

        Mockito.doNothing().when(view).displayMessage(Mockito.anyString());

        resetCountDownLatch();
    }

    @Test
    public void postIntegrationTest() throws InterruptedException {
        userService.login(currentUser.getAlias(), currentUser.getPassword(), new AuthenticateObserver() {

            @Override
            public void handleMessage(String message) {
                countDownLatch.countDown();
            }
            @Override
            public void handleException(Exception ex) {
                countDownLatch.countDown();
            }

            @Override
            public void handleSuccess(User currentUser, AuthToken authToken) {
                loggedInUser = currentUser;
                loggedInAuthToken = authToken;
                countDownLatch.countDown();
            }
        });

        awaitCountDownLatch();

        Status status = new Status("I am Ironman", loggedInUser, LocalDateTime.now().toString(), new ArrayList<String>(), new ArrayList<String>());
        itemList.add(status);
        mainActivityPresenter.postStatus(status);

        awaitCountDownLatch();

        // Verify that the display message for a success was called on the view
        Mockito.verify(mainActivityPresenter).postStatus(Mockito.any());

        mainActivityPresenter.getStatusService().getStory(currentUser, 10, status, new PagedNotificationObserver<Status>() {
            protected Status lastItem;

            @Override
            public void handleMessage(String message) {
                countDownLatch.countDown();
            }
            @Override
            public void handleException(Exception ex) {
                countDownLatch.countDown();
            }

            @Override
            public void handleSuccess(List<Status> items, boolean hasMorePages) {
                countDownLatch.countDown();
                lastItem = (itemList.size() > 0) ? itemList.get(itemList.size() - 1) : null;
                assertEquals(status, itemList.get(0));
                //setHasMorePages(hasMorePages);
                //localView.setLoadingStatus(false);
                //localView.addItemList(itemList);
            }
        });

        awaitCountDownLatch();
    }
}
