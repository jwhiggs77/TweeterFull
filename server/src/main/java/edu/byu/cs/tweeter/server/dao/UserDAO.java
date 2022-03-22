package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public interface UserDAO {

    RegisterResponse register(String firstName, String lastName, String username, String password, String image);
}
