package edu.byu.cs.tweeter.client.model.service.handler;

import edu.byu.cs.tweeter.client.model.service.handler.observer.AuthenticateObserver;

public abstract class AuthenticateHandler extends BackgroundTaskHandler<AuthenticateObserver> {

    public AuthenticateHandler(AuthenticateObserver observer) {
        super(observer);
    }
//    protected void handleSuccess(Bundle data, AuthenticateObserver observer);
}
