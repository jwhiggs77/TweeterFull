package edu.byu.cs.tweeter.server.service;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.concrete.DynamoStatusDAO;
import edu.byu.cs.tweeter.server.dao.concrete.DynamoUserDAO;
import edu.byu.cs.tweeter.server.dao.factory.DAOFactory;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDAOFactory;
import edu.byu.cs.tweeter.util.Pair;

public class ReturnStoriesTest {
    StatusService statusService;
    DAOFactory mockDAOFactory = mock(DynamoDAOFactory.class);
    StatusDAO mockStatusDAO = mock(DynamoStatusDAO.class);
    UserDAO mockUserDAO = mock(DynamoUserDAO.class);
    StoryRequest storyRequest;
    AuthToken authToken;
    User targetUser;
    int limit;
    Status lastStatus;
    Pair<List<Status>, Boolean> getStatusResponse;
    StoryResponse expectedResponse;

    @Before
    public void setUp() {
        statusService = new StatusService(mockDAOFactory);
        storyRequest = new StoryRequest(authToken, targetUser, limit, lastStatus);
        List<Status> expectedReturnedStories = new ArrayList<>();
        User user = new User("Tony", "Stark", "password", "imageURL");

        for (int i = 0; i < 3; i++) {
            Status status = new Status("post" + i, user, LocalDateTime.now().toString(), new ArrayList<String>(), new ArrayList<String>());
            expectedReturnedStories.add(status);
        }
        getStatusResponse = new Pair<>(expectedReturnedStories, true);
        expectedResponse = new StoryResponse(expectedReturnedStories, true);


        getStatusResponse.setSecond(true);
        getStatusResponse.setFirst(getStatusResponse.getFirst());

        //Mocking
        when(mockDAOFactory.makeStatusDAO()).thenReturn(mockStatusDAO);
        when(mockDAOFactory.makeUserDAO()).thenReturn(mockUserDAO);
        when(mockStatusDAO.getStory(storyRequest)).thenReturn(getStatusResponse);
        when(mockUserDAO.getUser(any())).thenReturn(new UserResponse(user));
    }

    @Test
    public void getStories() {
        StoryResponse actualResponse = statusService.getStory(storyRequest);
        assertTrue(actualResponse.isSuccess());
        assertNull(actualResponse.getMessage());
        for (int i = 0; i < expectedResponse.getStory().size(); i++) {
            assertEquals(expectedResponse.getStory().get(i), actualResponse.getStory().get(i));
        }
    }
}
