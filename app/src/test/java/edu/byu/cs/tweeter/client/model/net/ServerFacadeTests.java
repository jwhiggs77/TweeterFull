package edu.byu.cs.tweeter.client.model.net;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class ServerFacadeTests {
    private ServerFacade serverFacade;
    private User user;
    private FakeData fakeData;

    @Before
    public void setUp() {
        serverFacade = new ServerFacade();
        user = new User("firstName", "lastName", "@username", "image");
        fakeData = new FakeData();
    }

    @Test
    public void testRegister() throws IOException, TweeterRemoteException {
        String urlPath = "/register";
        RegisterResponse response = null;
        response = serverFacade.register(new RegisterRequest("firstName", "lastName", "username", "password", "image"), urlPath);
        assertNotNull(response);
        assertNotNull(response.getAuthToken());
        assertEquals(user, response.getUser());
        response = serverFacade.register(new RegisterRequest(null, "lastName", "username", "password", "image"), urlPath);
        assertFalse(response.isSuccess());
    }

    @Test
    public void testGetFollowers() throws IOException, TweeterRemoteException {
        String urlPath = "/getfollowers";
        FollowersResponse response = serverFacade.getFollowers(new FollowersRequest(new AuthToken(), "@user", 4, "@lastFollower"), urlPath);
        assertNotNull(response);
        assertNotNull(response.getFollowers());
        assertTrue(response.getFollowers().size() == 4);
        assertTrue(response.isSuccess());
        assertTrue(response.getFollowers().contains(fakeData.getFakeUsers().get(0)));
    }

    @Test
    public void testGetFollowersCount() throws IOException, TweeterRemoteException {
        String urlPath = "/followerscount";
        FollowersCountResponse response = serverFacade.getFollowersCount(new FollowersCountRequest(new AuthToken(), user), urlPath);
        assertNotNull(response);
        Integer testCount = fakeData.getFakeUsers().size();
        assertEquals(testCount, response.getCount());
    }

    @Test
    public void testGetFollowingCount() throws IOException, TweeterRemoteException {
        String urlPath = "/followingcount";
        FollowingCountResponse response = serverFacade.getFollowingCount(new FollowingCountRequest(new AuthToken(), user), urlPath);
        assertNotNull(response);
        Integer testCount = fakeData.getFakeUsers().size();
        assertEquals(testCount, response.getCount());
    }
}
