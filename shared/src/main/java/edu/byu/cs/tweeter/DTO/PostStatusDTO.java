package edu.byu.cs.tweeter.DTO;

import edu.byu.cs.tweeter.model.domain.Status;

public class PostStatusDTO {
    Status status;
    String alias;

    public PostStatusDTO(Status post, String alias) {
        this.status = post;
        this.alias = alias;
    }

    public Status getStatus() {
        return status;
    }

    public String getAlias() {
        return alias;
    }
}
