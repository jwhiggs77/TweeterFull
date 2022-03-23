package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.factory.DAOFactory;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {

    DAOFactory factory;

    public UserService(DAOFactory factory) {
        this.factory = factory;
    }

    public LoginResponse login(LoginRequest request) {
        if (request.getUsername() == null) return new LoginResponse("[BadRequest] Missing a username");
        if (request.getPassword() == null) return new LoginResponse("[BadRequest] Missing a password");

        return factory.makeUserDAO().login(request);
    }

    public LogoutResponse logout(LogoutRequest request) {
        return new LogoutResponse();
    }

    public RegisterResponse register(RegisterRequest request) {
        if (request.getUsername() == null) return new RegisterResponse("[BadRequest] Missing a username");
        if (request.getPassword() == null) return new RegisterResponse("[BadRequest] Missing a password");
        if (request.getFirstName() == null) return new RegisterResponse("[BadRequest] Missing a first name");
        if (request.getLastName() == null) return new RegisterResponse("[BadRequest] Missing a last name");

        factory.makePicDAO().upload(request.getImage());

        return factory.makeUserDAO().register(request);
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
