package edu.byu.cs.tweeter.model.net.response;

public class FollowingCountResponse extends Response {
    Integer count;

    public FollowingCountResponse(int count) {
        super(true);
        this.count = count;
    }

    FollowingCountResponse(String message) {
        super(false, message);
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
