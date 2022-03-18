package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends AuthenticatedTask {
    static final String URL_PATH = "/poststatus";

    /**
     * The new status being sent. Contains all properties of the status,
     * including the identity of the user sending the status.
     */
    private Status status;

    public PostStatusTask(AuthToken authToken, Status status, Handler messageHandler) {
        super(messageHandler, authToken);
        this.status = status;
    }

    @Override
    protected boolean processTask() throws IOException, TweeterRemoteException {
        PostStatusRequest request = new PostStatusRequest(getAuthToken(), status);
        PostStatusResponse response = getServerFacade().postStatus(request, URL_PATH);
        return response.isSuccess();
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {

    }
}
