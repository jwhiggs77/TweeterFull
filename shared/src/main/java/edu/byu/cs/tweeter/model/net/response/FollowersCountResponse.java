package edu.byu.cs.tweeter.model.net.response;

public class FollowersCountResponse extends Response {
    Integer count;

    public FollowersCountResponse(int count) {
        super(true);
        this.count = count;
    }

    FollowersCountResponse(String message) {
        super(false, message);
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
