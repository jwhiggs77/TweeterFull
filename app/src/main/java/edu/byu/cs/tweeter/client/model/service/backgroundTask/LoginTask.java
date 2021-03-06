package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {
    static final String URL_PATH = "/loginhandler";

    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler, username, password);
    }

    @Override
    protected Pair<Boolean, String> doLogin() throws IOException, TweeterRemoteException {
        LoginRequest request = new LoginRequest(getUsername(), getPassword());
        LoginResponse response = getServerFacade().login(request, URL_PATH);
        setAuthToken(response.getAuthToken());
        setCurrentUser(response.getUser());

        return new Pair<>(response.isSuccess(), response.getMessage());
    }
}
