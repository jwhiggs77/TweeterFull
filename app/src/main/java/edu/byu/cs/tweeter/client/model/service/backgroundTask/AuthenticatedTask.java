package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;

public abstract class AuthenticatedTask extends BackgroundTask {
    /**
     * Auth token for logged-in user.
     * This user is the "follower" in the relationship.
     */
    private final AuthToken authToken;

    public AuthToken getAuthToken() {
        return authToken;
    }

    protected AuthenticatedTask(Handler messageHandler, AuthToken authToken) {
        super(messageHandler);
        this.authToken = authToken;
    }

}
