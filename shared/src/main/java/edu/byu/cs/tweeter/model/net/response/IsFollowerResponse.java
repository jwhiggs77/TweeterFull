package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.User;

public class IsFollowerResponse extends Response {
    boolean isFollower;

    public IsFollowerResponse(boolean isFollower) {
        super(true);
        this.isFollower = isFollower;
    }

    IsFollowerResponse(String message) {
        super(false, message);
    }

    public boolean getIsFollower() {
        return isFollower;
    }

    public void setIsFollower(boolean follower) {
        isFollower = follower;
    }
}
