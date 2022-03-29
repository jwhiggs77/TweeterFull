package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.factory.DAOFactory;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService {
    DAOFactory factory;

    public StatusService(DAOFactory factory) {
        this.factory = factory;
    }

    public FeedResponse getFeed(FeedRequest request) {
        List<Status> feed = new ArrayList<>();
        Pair<ItemCollection<QueryOutcome>, Boolean> statuses = factory.makeStatusDAO().getFeed(request);

        for (Item statusItem : statuses.getFirst()) {
            User addUser = factory.makeUserDAO().getUser(new UserRequest(statusItem.getString("senderAlias"))).getUser();
            feed.add(new Status(statusItem.getString("post"), addUser, statusItem.getString("timeStamp"),
                    statusItem.getList("urls"), statusItem.getList("mentions")));
        }

        return new FeedResponse(feed, statuses.getSecond());
    }

    public StoryResponse getStory(StoryRequest request) {
        List<Status> stories = new ArrayList<>();
        Pair<ItemCollection<QueryOutcome>, Boolean> storyItems = factory.makeStatusDAO().getStory(request);

        for (Item statusItem : storyItems.getFirst()) {
            User addUser = factory.makeUserDAO().getUser(new UserRequest(statusItem.getString("senderAlias"))).getUser();
            stories.add(new Status(statusItem.getString("post"), addUser, statusItem.getString("timeStamp"),
                    statusItem.getList("urls"), statusItem.getList("mentions")));
        }

        return new StoryResponse(stories, storyItems.getSecond());
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        Pair<List<String>, Boolean> followers = factory.makeFollowDAO().getFollowers(new FollowersRequest(request.getAuthToken(), request.getStatus().getUser().getAlias(), 10000, null));
        return factory.makeStatusDAO().postStatus(request, followers.getFirst());
    }
}
