package edu.byu.cs.tweeter.server.service;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.DTO.PostStatusDTO;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.JsonSerializer;
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
        Pair<List<Status>, Boolean> storyItems = factory.makeStatusDAO().getStory(request);

        for (Status status : storyItems.getFirst()) {
            User addUser = factory.makeUserDAO().getUser(new UserRequest(status.getUser().getAlias())).getUser();
            status.setUser(addUser);
            stories.add(status);
        }

        return new StoryResponse(stories, storyItems.getSecond());
    }

//    public PostStatusResponse postStatus(PostStatusRequest request) {
//        Pair<List<String>, Boolean> followers = factory.makeFollowDAO().getFollowers(new FollowersRequest(request.getAuthToken(), request.getStatus().getUser().getAlias(), 10000, null));
//        return factory.makeStatusDAO().postStatus(request, followers.getFirst());
//    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        String queueUrl = "https://sqs.us-west-2.amazonaws.com/121950966543/postStatusQueue";

        factory.makeStatusDAO().postStory(request);

        SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(queueUrl).withMessageBody(JsonSerializer.serialize(request));
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);

        return new PostStatusResponse(request.getStatus());
    }

    public void postToFeeds(List<PostStatusDTO> statusDTOS) {
        factory.makeStatusDAO().addStatusBatch(statusDTOS);
    }
}
