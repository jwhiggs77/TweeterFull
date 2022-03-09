package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MainActivityPresenterTest {
    private MainActivityPresenter.View view;
    private UserService userService;
    private StatusService statusService;
    private Cache cache = Mockito.mock(Cache.class);
    private MainActivityPresenter mainActivityPresenterSpy;
    private Status status;

    @Before
    public void setup() {
        view = Mockito.mock(MainActivityPresenter.View.class);
        userService = Mockito.mock(UserService.class);
        statusService = Mockito.mock(StatusService.class);
        cache = Mockito.mock(Cache.class);
//        status = Mockito.mock(Status.class);

        mainActivityPresenterSpy = Mockito.spy(new MainActivityPresenter(view));
        //two ways to do it
//        1. Mockito.doReturn(userService).when(mainActivityPresenterSpy).getUserService();
//        2. Mockito.when(mainActivityPresenterSpy.getUserService()).thenReturn(userService);
        Mockito.when(mainActivityPresenterSpy.getUserService()).thenReturn(userService);
        Mockito.when(mainActivityPresenterSpy.getStatusService()).thenReturn(statusService);
        Cache.setInstance(cache);

        status = Mockito.mock(Status.class);
        Mockito.when(status.getPost()).thenReturn("This is a post");
    }

    @Test
    public void testLogout_isSuccessful() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainActivityPresenter.LogoutObserver observer = invocation.getArgument(0, MainActivityPresenter.LogoutObserver.class);
                observer.handleSuccess();
                return null;
            }
        };
        callLogout(answer);
        Mockito.verify(cache).clearCache();
        Mockito.verify(view).logout();
    }

    @Test
    public void testLogout_isError() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainActivityPresenter.LogoutObserver observer = invocation.getArgument(0, MainActivityPresenter.LogoutObserver.class);
                observer.handleMessage("error message");
                return null;
            }
        };
        testFailure(answer, "Failed to logout: error message");
    }

    @Test
    public void testLogout_isException() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainActivityPresenter.LogoutObserver observer = invocation.getArgument(0, MainActivityPresenter.LogoutObserver.class);
                observer.handleException(new Exception("exception message"));
                return null;
            }
        };
        testFailure(answer, "Failed to logout: exception message");
    }

    private void callLogout(Answer<Void> answer) {
        Mockito.doAnswer(answer).when(userService).logout(Mockito.any());
        mainActivityPresenterSpy.logout();
    }

    private void testFailure(Answer<Void> answer, String message) {
        callLogout(answer);
        Mockito.verify(view).displayMessage(message);
        Mockito.verify(cache, Mockito.times(0)).clearCache();
    }

    @Test
    public void testPost_isSuccessful() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Status newStatus = invocation.getArgument(0, Status.class);
                MainActivityPresenter.PostStatusObserver observer = invocation.getArgument(1, MainActivityPresenter.PostStatusObserver.class);
                observer.handleSuccess();
                assertNotNull(observer);
                assertEquals(newStatus.getPost(), status.getPost());
                return null;
            }
        };
        callPost(answer);
        Mockito.verify(view).post();
    }

    @Test
    public void testPost_isError() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainActivityPresenter.PostStatusObserver observer = invocation.getArgument(1, MainActivityPresenter.PostStatusObserver.class);
                observer.handleMessage("error message");
                return null;
            }
        };
        callPost(answer);
        Mockito.verify(view).displayMessage("Failed to post status: error message");
    }

    @Test
    public void testPost_isException() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                MainActivityPresenter.PostStatusObserver observer = invocation.getArgument(1, MainActivityPresenter.PostStatusObserver.class);
                observer.handleException(new Exception("exception message"));
                return null;
            }
        };
        callPost(answer);
        Mockito.verify(view).displayMessage("Failed to post status: exception message");
    }

    private void callPost(Answer<Void> answer) {
//        Status status = new Status("This is a post", new User(), "date", null, null);
        Status newStatus = Mockito.mock(Status.class);
        Mockito.when(newStatus.getPost()).thenReturn("This is a post");
        Mockito.doAnswer(answer).when(statusService).postStatus(Mockito.any(), Mockito.any());
        mainActivityPresenterSpy.postStatus(newStatus);
    }
}
