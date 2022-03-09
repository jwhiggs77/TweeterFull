package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.util.Pair;

public class AuthenticateTask extends BackgroundTask {

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

    private User currentUser;
    private AuthToken authToken;

    public AuthenticateTask(Handler messageHandler, String username, String password) {
        super(messageHandler);
        this.username = username;
        this.password = password;
    }

    private Pair<User, AuthToken> doLogin() {
        currentUser = getFakeData().getFirstUser();
        authToken = getFakeData().getAuthToken();
        return new Pair<>(currentUser, authToken);
    }

    @Override
    protected void processTask() {
        Pair<User, AuthToken> loginResult = doLogin();
        currentUser = loginResult.getFirst();
        authToken = loginResult.getSecond();
        sendSuccessMessage();
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putSerializable(USER_KEY, currentUser);
        msgBundle.putSerializable(AUTH_TOKEN_KEY, authToken);
    }
}
