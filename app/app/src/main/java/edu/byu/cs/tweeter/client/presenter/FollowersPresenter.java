package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User> {
    private FollowService followService;

    public FollowersPresenter(View view) {
        super(view);
        followService = new FollowService();
    }

    @Override
    public void getItems(AuthToken authToken, User user, int pageSize, User lastFollower) {
        followService.getFollowers(authToken, user, pageSize, lastFollower, new GetFollowersObserver());
    }

    public class GetFollowersObserver extends GetListObserver {
        @Override
        public String getMessageTag() {
            return "Failed to get followers: ";
        }
    }
}