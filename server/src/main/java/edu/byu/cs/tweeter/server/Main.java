package edu.byu.cs.tweeter.server;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.concrete.DynamoFollowDAO;
import edu.byu.cs.tweeter.server.dao.concrete.DynamoUserDAO;
import edu.byu.cs.tweeter.server.dao.factory.DAOFactory;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.lambda.FollowHandler;
import edu.byu.cs.tweeter.server.lambda.LoginHandler;
import edu.byu.cs.tweeter.server.lambda.LogoutHandler;
import edu.byu.cs.tweeter.server.lambda.RegisterHandler;
import edu.byu.cs.tweeter.server.service.FollowService;

public class Main {
    public static void main(String args[]) {
        User josh = new User("Josh", "Higgins", "@higgi", "password", "https://s3.us-west-2.amazonaws.com/org.higgi27.tweeter.profile.picture.bucket/profilePic");
        User courtney = new User("Courtney", "Gray", "@clearEyes", "password", "https://s3.us-west-2.amazonaws.com/org.higgi27.tweeter.profile.picture.bucket/profilePic");
        User user = new User("user", "1", "@user1", "password", "https://s3.us-west-2.amazonaws.com/org.higgi27.tweeter.profile.picture.bucket/profilePic");
        User user10 = new User("user", "10", "@user10", "password", "https://s3.us-west-2.amazonaws.com/org.higgi27.tweeter.profile.picture.bucket/profilePic");
        User ironman = new User("Tony", "Stark", "@ironman", "password", "https://s3.us-west-2.amazonaws.com/org.higgi27.tweeter.profile.picture.bucket/profilePic");

//        LoginRequest loginRequest = new LoginRequest(josh.getAlias(), "password");
//        LoginHandler handler = new LoginHandler();
//        LoginResponse loginResponse = handler.handleRequest(loginRequest, null);

        DynamoUserDAO dynamoUserDAO = new DynamoUserDAO();
        DynamoFollowDAO dynamoFollowDAO = new DynamoFollowDAO();
        List<User> userList = new ArrayList<>();
        List<List<String>> followList = new ArrayList<>();
        int count = 264;
        for (int i = 0; i < 10000; i++) {
            User temp = new User("user", Integer.toString(i), "@user"+i, "password", "https://s3.us-west-2.amazonaws.com/org.higgi27.tweeter.profile.picture.bucket/profilePic");
//            RegisterRequest registerRequest = new RegisterRequest("dude", Integer.toString(i), "@dude" + i, "password", "https://s3.us-west-2.amazonaws.com/org.higgi27.tweeter.profile.picture.bucket/profilePic");
//            RegisterHandler registerHandler = new RegisterHandler();
//            RegisterResponse registerResponse = registerHandler.handleRequest(registerRequest, null);
            userList.add(temp);

            List<String> followListItem = new ArrayList<>();
            //follower_handle
            followListItem.add(temp.getAlias());
            //followee_handle
            followListItem.add(josh.getAlias());
            //follower_name
            followListItem.add(temp.getName());
            //followee_name
            followListItem.add(josh.getName());

            followList.add(followListItem);

            if (userList.size() >= 25) {
                dynamoUserDAO.addUserBatch(userList);
                userList = new ArrayList<>();

                dynamoFollowDAO.addFollowBatch(followList);
                followList = new ArrayList<>();
                System.out.println("Sent batch: " + count);
                count++;
            }

//            LogoutHandler logoutHandler = new LogoutHandler();
//            LogoutRequest logoutRequest = new LogoutRequest(registerResponse.getAuthToken());
//            logoutHandler.handleRequest(logoutRequest, null);

        }
        dynamoUserDAO.addUserBatch(userList);
        dynamoFollowDAO.addFollowBatch(followList);
    }
}
