package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> {
    private StatusService statusService;

    public StoryPresenter(View view) {
        super(view);
        statusService = new StatusService();
    }

    @Override
    public void getItems(AuthToken authToken, User user, int pageSize, Status lastItem) {
        statusService.getStory(user, pageSize, lastItem, new GetStoryObserver());
    }

    public class GetStoryObserver extends GetListObserver {
        @Override
        public String getMessageTag() {
            return "Failed to get story: ";
        }
    }
}
