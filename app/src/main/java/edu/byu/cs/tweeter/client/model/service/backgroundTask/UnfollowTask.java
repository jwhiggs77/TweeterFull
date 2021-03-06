package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends AuthenticatedTask {
    public static final String URL_PATH = "/unfollow";

    /**
     * The user that is being followed.
     */
    private User followee;

    public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(messageHandler, authToken);
        this.followee = followee;
    }

    @Override
    protected Pair<Boolean, String> processTask() throws IOException, TweeterRemoteException {
        UnfollowRequest request = new UnfollowRequest(followee, getAuthToken());
        UnfollowResponse response = getServerFacade().unfollow(request, URL_PATH);
        return new Pair<>(response.isSuccess(), response.getMessage());
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {

    }
}
