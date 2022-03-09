package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.handler.observer.ServiceObserver;

public abstract class Presenter {
    public interface View {
        void displayMessage(String message);
    }

    private View view;

    public Presenter(View view) {
        this.view = view;
    }

    public abstract class Observer implements ServiceObserver {

        public abstract String getMessageTag();

        @Override
        public void handleMessage(String message) {
            view.displayMessage(getMessageTag() + message);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage(getMessageTag() + ex.getMessage());
        }
    }
}
