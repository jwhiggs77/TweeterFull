package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;

public interface UserDAO {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    UserResponse getUser(UserRequest request);

    User getUserFromToken(AuthToken authToken);

    void updateFollowCount(User currentUser, Integer count, boolean iterate, String followerCount);

    FollowersCountResponse getFollowerCount(User targetUser);

    FollowingCountResponse getFolloweeCount(User targetUser);

    LogoutResponse logout(LogoutRequest request);
}
