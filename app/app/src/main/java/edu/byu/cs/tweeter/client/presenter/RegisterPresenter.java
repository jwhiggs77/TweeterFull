package edu.byu.cs.tweeter.client.presenter;

import android.widget.ImageView;
import edu.byu.cs.tweeter.client.model.service.UserService;

public class RegisterPresenter extends AuthenticatePresenter {

    public RegisterPresenter(View view) {
        super(view);
        this.view = view;
        userService = new UserService();
    }

    public class RegisterObserver extends AuthenticateObserver {
        @Override
        public String getMessageTag() {
            return "Failed to register: ";
        }
    }

    public void register(String firstName, String lastName, String alias, String password, String imageBytesBase64) {
        userService.register(firstName, lastName, alias, password, imageBytesBase64, new RegisterObserver());
    }

    public void validateRegistration(String firstName, String lastName, String alias, String password, ImageView imageToUpload) {
        if (firstName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (alias.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (imageToUpload.getDrawable() == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
        validate(alias, password);
    }
}
