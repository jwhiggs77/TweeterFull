package edu.byu.cs.tweeter.model.net.response;

public class LogoutResponse extends Response{
    public LogoutResponse() {
        super(true);
    }

    LogoutResponse(String message) {
        super(false, message);
    }
}
