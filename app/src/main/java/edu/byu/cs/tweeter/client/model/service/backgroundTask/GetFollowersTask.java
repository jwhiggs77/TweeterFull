package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.util.Pair;

import java.io.IOException;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedTask<User> {
    static final String URL_PATH = "/getfollowers";

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(messageHandler, authToken, targetUser, limit, lastFollower);
    }
    
    @Override
    protected Pair<Boolean, String> getItems() throws IOException, TweeterRemoteException {
        String lastAlias = getLastItem() == null ? null: getLastItem().getAlias();
        FollowersRequest request = new FollowersRequest(getAuthToken(), getTargetUser().getAlias(), getLimit(), lastAlias);
        FollowersResponse response = getServerFacade().getFollowers(request, URL_PATH);
        setItems(response.getFollowers());
        setHasMorePages(response.getHasMorePages());
        return new Pair<>(response.isSuccess(), response.getMessage());
    }
}
