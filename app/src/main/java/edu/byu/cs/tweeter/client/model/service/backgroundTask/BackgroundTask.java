package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class BackgroundTask implements Runnable {
    private static final String LOG_TAG = "BackgroundTask";
    public static final String SUCCESS_KEY = "success";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_KEY = "exception";

    /**
     * Message handler that will receive task results.
     */
    private final Handler messageHandler;

    protected BackgroundTask(Handler messageHandler) {
        this.messageHandler = messageHandler;
    }

    protected void sendSuccessMessage() throws IOException, TweeterRemoteException {
        Bundle msgBundle = createBundle(true);
        loadSuccessBundle(msgBundle);
        sendMessage(msgBundle);
    }

    @Override
    public void run() {
        try {
            processTask();
        } catch (Exception ex) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            sendExceptionMessage(ex);
        }
    }

    protected abstract boolean processTask() throws IOException, TweeterRemoteException;

    protected abstract void loadSuccessBundle(Bundle msgBundle) throws IOException, TweeterRemoteException;

    protected void sendFailedMessage(String message) {
        Bundle msgBundle = createBundle(false);
        msgBundle.putString(MESSAGE_KEY, message);
        sendMessage(msgBundle);
    }

    protected void sendExceptionMessage(Exception exception) {
        Bundle msgBundle = createBundle(false);
        msgBundle.putSerializable(EXCEPTION_KEY, exception);
        sendMessage(msgBundle);
    }

    private void sendMessage(Bundle msgBundle) {
        Message msg = Message.obtain();
        msg.setData(msgBundle);
        messageHandler.sendMessage(msg);
    }

    @NotNull
    private Bundle createBundle(boolean value) {
        Bundle msgBundle = new Bundle();
        msgBundle.putBoolean(SUCCESS_KEY, value);
        return msgBundle;
    }

//    protected FakeData getFakeData() {
//        return new FakeData();
//    }

    public ServerFacade getServerFacade() {
        return new ServerFacade();
    }
}
