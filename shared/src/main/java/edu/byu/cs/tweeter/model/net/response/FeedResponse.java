package edu.byu.cs.tweeter.model.net.response;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class FeedResponse extends PagedResponse {
    List<Status> feed;

    public FeedResponse(List<Status> feed, boolean hasMorePages) {
        super(true, hasMorePages);
        this.feed = feed;
    }

    FeedResponse(String message) {
        super(false, message, false);
    }

    public List<Status> getFeed() {
        return feed;
    }
}
