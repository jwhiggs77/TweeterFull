package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class FollowersRequest {
    private AuthToken authToken;
    private String followingAlias;
    private int limit;
    private String lastFollowerAlias;

    private FollowersRequest() {}

    public FollowersRequest(AuthToken authToken, String followingAlias, int limit, String lastFollowerAlias) {
        this.authToken = authToken;
        this.followingAlias = followingAlias;
        this.limit = limit;
        this.lastFollowerAlias = lastFollowerAlias;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getFollowingAlias() {
        return followingAlias;
    }

    public void setFollowingAlias(String followingAlias) {
        this.followingAlias = followingAlias;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getLastFollowerAlias() {
        return lastFollowerAlias;
    }

    public void setLastFollowerAlias(String lastFollowerAlias) {
        this.lastFollowerAlias = lastFollowerAlias;
    }
}
