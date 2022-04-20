package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {
    public static final String URL_PATH = "/followingcount";

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(messageHandler, authToken, targetUser);
    }

    @Override
    protected Pair<Boolean, String> getCount() throws IOException, TweeterRemoteException {
        FollowingCountRequest request = new FollowingCountRequest(getAuthToken(), getTargetUser());
        FollowingCountResponse response = getServerFacade().getFollowingCount(request, URL_PATH);
        setCount(response.getCount());
        return new Pair<>(response.isSuccess(), response.getMessage());
    }
}
