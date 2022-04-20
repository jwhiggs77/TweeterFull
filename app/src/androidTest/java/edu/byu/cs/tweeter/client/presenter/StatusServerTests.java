package edu.byu.cs.tweeter.client.presenter;

import android.os.Looper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.handler.observer.PagedNotificationObserver;
import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
import edu.byu.cs.tweeter.client.presenter.PagedPresenter.GetListObserver;
import edu.byu.cs.tweeter.client.presenter.StoryPresenter.GetStoryObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class StatusServerTests {
    private StatusService statusService;
    private FakeData fakeData;
    private GetStoryObserver observer;

    @Before
    public void setUp() {
        statusService = new StatusService();
        fakeData = new FakeData();
        observer = Mockito.mock(GetStoryObserver.class);
    }

    private interface GetStoryObserver extends PagedNotificationObserver<Status> {}

    @Test
    public void testGetStory() {
        Looper.prepare();
        Mockito.doAnswer(invocation -> {
            List<Status> items = invocation.getArgument(0);
            Boolean hasMorePages = invocation.getArgument(1);
            Assert.assertTrue(hasMorePages);
            Assert.assertEquals(fakeData.getFakeStatuses().size(), items.size());
            for (int i = 0; i < items.size(); i++) {
                Assert.assertEquals(fakeData.getFakeStatuses().get(i).post, items.get(i).post);
                Assert.assertEquals(fakeData.getFakeStatuses().get(i).user, items.get(i).user);
                Assert.assertEquals(fakeData.getFakeStatuses().get(i).mentions, items.get(i).mentions);
                Assert.assertEquals(fakeData.getFakeStatuses().get(i).urls, items.get(i).urls);
            }
            Looper.myLooper().quitSafely();
            return null;
        }).when(observer).handleSuccess(Mockito.any(), Mockito.anyBoolean());


        statusService.getStory(fakeData.getFirstUser(), fakeData.getFakeStatuses().size(), null, observer);
        Looper.loop();
    }
}
