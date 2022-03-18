package edu.byu.cs.tweeter.model.net.response;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;

public class StoryResponse extends PagedResponse {
    List<Status> story;

    public StoryResponse(List<Status> story, boolean hasMorePages) {
        super(true, hasMorePages);
        this.story = story;
    }

    StoryResponse(boolean success, String message, boolean hasMorePages) {
        super(success, message, hasMorePages);
    }

    public List<Status> getStory() {
        return story;
    }
}
