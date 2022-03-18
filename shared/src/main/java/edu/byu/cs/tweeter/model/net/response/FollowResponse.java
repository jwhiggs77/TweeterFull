package edu.byu.cs.tweeter.model.net.response;

public class FollowResponse extends Response {
    public FollowResponse() {
        super(true);
    }

    FollowResponse(String message) {
        super(false, message);
    }
}
