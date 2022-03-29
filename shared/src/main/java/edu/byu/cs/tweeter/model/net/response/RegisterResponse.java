package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterResponse extends Response {

    User user;
    AuthToken authToken = new AuthToken();

    public RegisterResponse(String firstName, String lastName, String username, String password, String image, AuthToken authToken) {
        super(true);
        this.user = new User(firstName, lastName, username, password, image);
        this.authToken = authToken;
    }

    public User getUser() {
        return user;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public RegisterResponse(String message) {
        super(false, message);
    }
}
