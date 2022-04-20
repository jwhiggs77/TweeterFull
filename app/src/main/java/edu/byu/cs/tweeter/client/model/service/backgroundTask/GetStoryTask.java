package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.Pair;

import java.io.IOException;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedTask<Status> {
    static final String URL_PATH = "/getstory";

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(messageHandler, authToken, targetUser, limit, lastStatus);
    }

    @Override
    protected Pair<Boolean, String> getItems() throws IOException, TweeterRemoteException {
        StoryRequest request = new StoryRequest(getAuthToken(), getTargetUser(), getLimit(), getLastItem());
        StoryResponse response = getServerFacade().getStory(request, URL_PATH);
        setItems(response.getStory());
        setHasMorePages(response.getHasMorePages());
        return new Pair<>(response.isSuccess(), response.getMessage());
    }
}