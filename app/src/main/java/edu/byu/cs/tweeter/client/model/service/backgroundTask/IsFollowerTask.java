package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.util.Pair;

import java.io.IOException;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthenticatedTask {
    public static final String IS_FOLLOWER_KEY = "is-follower";
    public static final String URL_PATH = "/isfollower";

    /**
     * The alleged follower.
     */
    private User follower;
    /**
     * The alleged followee.
     */
    private User followee;

    private boolean isFollower;

    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(messageHandler, authToken);
        this.follower = follower;
        this.followee = followee;
    }

    @Override
    protected Pair<Boolean, String> processTask() throws IOException, TweeterRemoteException {
        IsFollowerRequest request = new IsFollowerRequest(getAuthToken(), follower, followee);
        IsFollowerResponse response = getServerFacade().isFollower(request, URL_PATH);
        isFollower = response.getIsFollower();
        System.out.println("IS_FOLLOWER: " + response.getIsFollower());
        return new Pair<>(response.isSuccess(), response.getMessage());
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);
    }
}
