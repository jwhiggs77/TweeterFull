package edu.byu.cs.tweeter.model.net.response;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

public class PostStatusResponse extends Response {
    Status status;

    public PostStatusResponse(Status status) {
        super(true);
        this.status = status;
    }

    PostStatusResponse(String message) {
        super(false, message);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
