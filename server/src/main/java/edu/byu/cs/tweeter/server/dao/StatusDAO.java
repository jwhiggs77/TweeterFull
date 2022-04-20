package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;

import java.util.List;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.util.Pair;

public interface StatusDAO {
    Pair<ItemCollection<QueryOutcome>, Boolean> getFeed(FeedRequest request);

    Pair<ItemCollection<QueryOutcome>, Boolean> getStory(StoryRequest request);

    PostStatusResponse postStatus(PostStatusRequest request, List<String> first);
}
