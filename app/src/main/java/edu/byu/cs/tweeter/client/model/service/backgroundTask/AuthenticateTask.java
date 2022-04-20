package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.util.Pair;

public abstract class AuthenticateTask extends BackgroundTask {

    public static final String USER_KEY = "user";
    public static final String AUTH_TOKEN_KEY = "auth-token";

    /**
     * The user's username (or "alias" or "handle"). E.g., "@susan".
     */
    private final String username;
    /**
     * The user's password.
     */
    private final String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private User currentUser;

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private AuthToken authToken;

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public AuthenticateTask(Handler messageHandler, String username, String password) {
        super(messageHandler);
        this.username = username;
        this.password = password;
    }

    protected abstract Pair<Boolean, String> doLogin() throws IOException, TweeterRemoteException;

    @Override
    protected boolean processTask() throws IOException, TweeterRemoteException {
        Pair<Boolean, String> response = doLogin();

        if (response.getFirst()) {
            sendSuccessMessage();
        }
        else {
            sendFailedMessage(response.getSecond());
        }


        return true;
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, currentUser);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);
    }
}
