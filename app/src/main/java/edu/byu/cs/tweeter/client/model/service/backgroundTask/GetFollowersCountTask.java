package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import java.io.IOException;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;

/**
 * Background task that queries how many followers a user has.
 */
public class GetFollowersCountTask extends GetCountTask {
    public static final String URL_PATH = "/followerscount";

    public GetFollowersCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(messageHandler, authToken, targetUser);
    }

    @Override
    protected int getCount() throws IOException, TweeterRemoteException {
        FollowersCountRequest request = new FollowersCountRequest(getAuthToken(), getTargetUser());
        FollowersCountResponse response = getServerFacade().getFollowersCount(request, URL_PATH);
        return response.getCount();
    }
}
