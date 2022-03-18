package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends AuthenticatedTask {
    public static final String USER_KEY = "user";
    static final String URL_PATH = "/getuser";

    /**
     * Alias (or handle) for user whose profile is being retrieved.
     */
    private String alias;
    private User returnedUser;

    public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
        super(messageHandler, authToken);
        this.alias = alias;
    }

    @Override
    protected boolean processTask() throws IOException, TweeterRemoteException {
        // TODO: This is empty only cuz we're using dummy data
        UserRequest request = new UserRequest(alias);
        UserResponse response = getServerFacade().getUser(request, URL_PATH);
        returnedUser = response.getUser();
        return response.isSuccess();
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) throws IOException, TweeterRemoteException {
        msgBundle.putSerializable(USER_KEY, returnedUser);
    }
}
