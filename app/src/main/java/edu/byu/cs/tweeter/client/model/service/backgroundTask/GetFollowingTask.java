package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.Pair;

import java.io.IOException;
import java.util.List;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends PagedTask<User> {
    static final String URL_PATH = "/getfollowing";

    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                            Handler messageHandler) {
        super(messageHandler, authToken, targetUser, limit, lastFollowee);
    }

    @Override
    protected Pair<List<User>, Boolean> getItems() throws IOException, TweeterRemoteException {
        String lastAlias = getLastItem() == null ? null: getLastItem().getAlias();
        FollowingRequest request = new FollowingRequest(getAuthToken(), getTargetUser().getAlias(), getLimit(), lastAlias);
        FollowingResponse response = getServerFacade().getFollowees(request, URL_PATH);
        return new Pair<>(response.getFollowees(), response.isSuccess());
    }
}
