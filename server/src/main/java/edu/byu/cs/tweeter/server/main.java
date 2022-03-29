package edu.byu.cs.tweeter.server;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.lambda.FollowHandler;
import edu.byu.cs.tweeter.server.lambda.GetFeedHandler;
import edu.byu.cs.tweeter.server.lambda.GetFollowersCountHandler;
import edu.byu.cs.tweeter.server.lambda.GetFollowersHandler;
import edu.byu.cs.tweeter.server.lambda.GetFollowingCountHandler;
import edu.byu.cs.tweeter.server.lambda.GetFollowingHandler;
import edu.byu.cs.tweeter.server.lambda.GetStoryHandler;
import edu.byu.cs.tweeter.server.lambda.IsFollowerHandler;
import edu.byu.cs.tweeter.server.lambda.LoginHandler;
import edu.byu.cs.tweeter.server.lambda.PostStatusHandler;
import edu.byu.cs.tweeter.server.lambda.RegisterHandler;
import edu.byu.cs.tweeter.server.lambda.UnfollowHandler;

public class main {
    public static void main(String args[]) {
        User josh = new User("Josh", "Higgins", "@higgi", "password", "https://s3.us-west-2.amazonaws.com/org.higgi27.tweeter.profile.picture.bucket/profilePic");
        User courtney = new User("Courtney", "Gray", "@clearEyes", "password", "https://s3.us-west-2.amazonaws.com/org.higgi27.tweeter.profile.picture.bucket/profilePic");
        User user = new User("user", "1", "@user1", "password", "https://s3.us-west-2.amazonaws.com/org.higgi27.tweeter.profile.picture.bucket/profilePic");
        User user10 = new User("user", "10", "@user10", "password", "https://s3.us-west-2.amazonaws.com/org.higgi27.tweeter.profile.picture.bucket/profilePic");


        LoginRequest loginRequest = new LoginRequest("@higgi", "bad");
        LoginHandler handler = new LoginHandler();
        LoginResponse loginResponse = handler.handleRequest(loginRequest, null);

//        for (int i = 1; i < 11; i++) {
//            RegisterRequest registerRequest = new RegisterRequest("user", Integer.toString(i), "@user" + i, "password", "https");
//            RegisterHandler registerHandler = new RegisterHandler();
//            RegisterResponse registerResponse = registerHandler.handleRequest(registerRequest, null);

//            FollowRequest followRequest = new FollowRequest(authToken, registerResponse.getUser());
//            FollowHandler followHandler = new FollowHandler();
//            FollowResponse followResponse = followHandler.handleRequest(followRequest, null);
//
//            FollowRequest followRequest1 = new FollowRequest(registerResponse.getAuthToken(), josh);
//            FollowResponse followResponse1 = followHandler.handleRequest(followRequest1, null);

//            List<String> mentions = new ArrayList<String>();
//            List<String> urls = new ArrayList<String>();
//            PostStatusRequest postStatusRequest = new PostStatusRequest(loginResponse.getAuthToken(), new Status("post" + i, user, LocalDateTime.now().toString(), urls, mentions));
//            PostStatusHandler postStatusHandler = new PostStatusHandler();
//            PostStatusResponse postStatusResponse = postStatusHandler.handleRequest(postStatusRequest, null);
//        }

//        FeedRequest feedRequest = new FeedRequest(loginResponse.getAuthToken(), josh, 10, null);
//        GetFeedHandler feedHandler = new GetFeedHandler();
//        FeedResponse feedResponse = feedHandler.handleRequest(feedRequest, null);
////
//        StoryRequest storyRequest = new StoryRequest(loginResponse.getAuthToken(), josh, 10, null);
//        GetStoryHandler getStoryHandler = new GetStoryHandler();
//        StoryResponse storyResponse = getStoryHandler.handleRequest(storyRequest, null);
//
//        FollowersRequest followersRequest = new FollowersRequest(loginResponse.getAuthToken(), "@higgi", 10, null);
//        GetFollowersHandler getFollowersHandler = new GetFollowersHandler();
//        FollowersResponse followersResponse = getFollowersHandler.handleRequest(followersRequest, null);
//
//        FollowingRequest followingRequest = new FollowingRequest(loginResponse.getAuthToken(), "@higgi", 10, null);
//        GetFollowingHandler getFollowingHandler = new GetFollowingHandler();
//        FollowingResponse followingResponse = getFollowingHandler.handleRequest(followingRequest, null);
//
////        List<String> mentions = new ArrayList<String>(Arrays.asList("@alias"));
////        List<String> urls = new ArrayList<String>(Arrays.asList("url1, url2"));
////        PostStatusRequest postStatusRequest = new PostStatusRequest(null, new Status("post9", josh, LocalDateTime.now().toString(), urls, mentions));
////        PostStatusHandler postStatusHandler = new PostStatusHandler();
////        PostStatusResponse postStatusResponse = postStatusHandler.handleRequest(postStatusRequest, null);
//
////        FollowRequest followRequest = new FollowRequest(authToken, registerResponse.getUser());
////        FollowHandler followHandler = new FollowHandler();
////        FollowResponse followResponse = followHandler.handleRequest(followRequest, null);
//
//        FollowersCountRequest followersCountRequest = new FollowersCountRequest(authToken, josh);
//        GetFollowersCountHandler getFollowersCountHandler = new GetFollowersCountHandler();
//        FollowersCountResponse followersCountResponse = getFollowersCountHandler.handleRequest(followersCountRequest, null);
//
//        FollowingCountRequest followingCountRequest = new FollowingCountRequest(authToken, josh);
//        GetFollowingCountHandler getFollowingCountHandler = new GetFollowingCountHandler();
//        FollowingCountResponse followingCountResponse = getFollowingCountHandler.handleRequest(followingCountRequest, null);

//        IsFollowerRequest isFollowerRequest = new IsFollowerRequest(loginResponse.getAuthToken(), josh, user);
//        IsFollowerHandler isFollowerHandler = new IsFollowerHandler();
//        IsFollowerResponse isFollowerResponse = isFollowerHandler.handleRequest(isFollowerRequest, null);

//        UnfollowRequest unfollowRequest = new UnfollowRequest(user10, loginResponse.getAuthToken());
//        UnfollowHandler unfollowHandler = new UnfollowHandler();
//        UnfollowResponse unfollowResponse = unfollowHandler.handleRequest(unfollowRequest, null);
        int x = 0;
    }
}
