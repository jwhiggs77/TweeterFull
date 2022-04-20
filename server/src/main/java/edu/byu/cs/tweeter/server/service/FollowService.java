package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.concrete.DynamoFollowDAO;
import edu.byu.cs.tweeter.server.dao.factory.DAOFactory;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService {

    DAOFactory factory;

    public FollowService(DAOFactory factory) {
        this.factory = factory;
    }

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link DynamoFollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        List<User> userList = new ArrayList<>();
        Pair<List<String>, Boolean> followees = factory.makeFollowDAO().getFollowees(request);

        for (String alias : followees.getFirst()) {
            userList.add(factory.makeUserDAO().getUser(new UserRequest(alias)).getUser());
        }

        return new FollowingResponse(userList, followees.getSecond());
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getFollowingAlias() == null) {
            throw new RuntimeException("[BadRequest] Request needs to have a following alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[BadRequest] Request needs to have a positive limit");
        }
        List<User> userList = new ArrayList<>();
        Pair<List<String>, Boolean> followers = factory.makeFollowDAO().getFollowers(request);

        for (String alias : followers.getFirst()) {
            userList.add(factory.makeUserDAO().getUser(new UserRequest(alias)).getUser());
        }

        return new FollowersResponse(userList, followers.getSecond());
    }

    public FollowersCountResponse getFollowersCount(FollowersCountRequest request) {
        return factory.makeUserDAO().getFollowerCount(request.getTargetUser());
    }

    public FollowingCountResponse getFollowingCount(FollowingCountRequest request) {
        return factory.makeUserDAO().getFolloweeCount(request.getTargetUser());
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        return factory.makeFollowDAO().isFollower(request);
    }

    public FollowResponse follow(FollowRequest request) {
        User currentUser = factory.makeUserDAO().getUserFromToken(request.getAuthToken());
        FollowResponse followResponse = factory.makeFollowDAO().follow(request, currentUser);
        if (followResponse.isSuccess()) {
            FollowingCountResponse followingCount = factory.makeUserDAO().getFolloweeCount(currentUser);
            factory.makeUserDAO().updateFollowCount(currentUser, followingCount.getCount(), true, "followingCount");

            FollowersCountResponse followerCount = factory.makeUserDAO().getFollowerCount(request.getFollowee());
            factory.makeUserDAO().updateFollowCount(request.getFollowee(), followerCount.getCount(), true, "followerCount");
        }
        return followResponse;
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        User currentUser = factory.makeUserDAO().getUserFromToken(request.getAuthToken());
        UnfollowResponse unfollowResponse = factory.makeFollowDAO().unfollow(request, currentUser);
        if (unfollowResponse.isSuccess()) {
            FollowingCountResponse followingCount = factory.makeUserDAO().getFolloweeCount(currentUser);
            factory.makeUserDAO().updateFollowCount(currentUser, followingCount.getCount(), false, "followingCount");

            FollowersCountResponse followersCount = factory.makeUserDAO().getFollowerCount(request.getFollowee());
            factory.makeUserDAO().updateFollowCount(request.getFollowee(), followersCount.getCount(), false, "followerCount");
        }
        return unfollowResponse;
    }
}
