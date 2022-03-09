package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;

public class LoginPresenter extends AuthenticatePresenter {

    public LoginPresenter(View view) {
        super(view);
        this.view = view;
        userService = new UserService();
    }

    public class LoginObserver extends AuthenticateObserver {
        @Override
        public String getMessageTag() {
            return "Failed to login: ";
        }
    }

    public void login(String alias, String password) {
        userService.login(alias, password, new LoginObserver());
    }

    public void validateLogin(String alias, String password) {
        validate(alias, password);
    }
}
