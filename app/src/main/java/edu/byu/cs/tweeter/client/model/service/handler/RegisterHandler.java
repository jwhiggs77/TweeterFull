package edu.byu.cs.tweeter.client.model.service.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.handler.observer.AuthenticateObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterHandler extends AuthenticateHandler {

    public RegisterHandler(AuthenticateObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccess(Bundle data, AuthenticateObserver observer) {
        User registeredUser = (User) data.getSerializable(RegisterTask.USER_KEY);
        AuthToken authToken = (AuthToken) data.getSerializable(RegisterTask.AUTH_TOKEN_KEY);
        observer.handleSuccess(registeredUser, authToken);
    }
}
