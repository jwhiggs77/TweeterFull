package edu.byu.cs.tweeter.server.service;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.factory.DAOFactory;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {

    DAOFactory factory;

    public UserService(DAOFactory factory) {
        this.factory = factory;
    }


//    UserDAO getUserDAO() {
//        return new UserDAO();
//    }

    public LoginResponse login(LoginRequest request) {
        if (request.getUsername() != null) return new LoginResponse("[BadRequest] Missing a username");
        if (request.getPassword() != null) return new LoginResponse("[BadRequest] Missing a password");

        // TODO: Generates dummy data. Replace with a real implementation.
        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new LoginResponse(user, authToken);
    }

    public LogoutResponse logout(LogoutRequest request) {
        return new LogoutResponse();
    }

    public RegisterResponse register(RegisterRequest request) {
        if (request.getUsername() != null) return new RegisterResponse("[BadRequest] Missing a username");
        if (request.getPassword() != null) new RegisterResponse("[BadRequest] Missing a password");
        if (request.getFirstName() != null) new RegisterResponse("[BadRequest] Missing a first name");
        if (request.getLastName() != null) new RegisterResponse("[BadRequest] Missing a last name");

        return factory.makeUserDAO().register(request.getFirstName(), request.getLastName(), request.getUsername(), request.getPassword(), request.getImage());

        // TODO: Generates dummy data. Replace with a real implementation.
//        return new RegisterResponse("firstName", "lastName", "@username", "password", "image");
    }

//    private <T> T checkInput(String input, T response) {
//        if(input == null){
////            throw new RuntimeException("[BadRequest] Missing a " + message);
//            return response;
//        }
//        return null;
//    }

    public UserResponse getUser(UserRequest request) {
        return new UserResponse(getFakeData().findUserByAlias(request.getAlias()));
    }

    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return new FakeData();
    }
}
