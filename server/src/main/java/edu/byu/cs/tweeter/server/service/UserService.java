package edu.byu.cs.tweeter.server.service;

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
        return factory.makeUserDAO().logout(request);
    }

    public RegisterResponse register(RegisterRequest request) {
        if (request.getUsername() == null) return new RegisterResponse("[BadRequest] Missing a username");
        if (request.getPassword() == null) return new RegisterResponse("[BadRequest] Missing a password");
        if (request.getFirstName() == null) return new RegisterResponse("[BadRequest] Missing a first name");
        if (request.getLastName() == null) return new RegisterResponse("[BadRequest] Missing a last name");

        factory.makePicDAO().upload(request.getImage(), request.getUsername());

        return factory.makeUserDAO().register(request);
    }

    public UserResponse getUser(UserRequest request) {
        return factory.makeUserDAO().getUser(request);
    }
}
