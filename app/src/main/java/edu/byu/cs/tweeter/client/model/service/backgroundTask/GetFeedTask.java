package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.util.Pair;

import java.io.IOException;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedTask<Status> {
    static final String URL_PATH = "/getfeed";

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(messageHandler, authToken, targetUser, limit, lastStatus);
    }

    @Override
    protected Pair<Boolean, String> getItems() throws IOException, TweeterRemoteException {
        FeedRequest request = new FeedRequest(getAuthToken(), getTargetUser(), getLimit(), getLastItem());
        FeedResponse response = getServerFacade().getFeed(request, URL_PATH);
        setItems(response.getFeed());
        setHasMorePages(response.getHasMorePages());
        return new Pair<>(response.isSuccess(), response.getMessage());
    }
}
