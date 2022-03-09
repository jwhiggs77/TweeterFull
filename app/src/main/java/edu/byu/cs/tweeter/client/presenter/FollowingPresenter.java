package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User> {
    private FollowService followService;

    public FollowingPresenter(View view) {
        super(view);
        followService = new FollowService();
    }

    @Override
    public void getItems(AuthToken authToken, User user, int pageSize, User lastItem) {
        followService.getFollowing(Cache.getInstance().getCurrUserAuthToken(), user, pageSize, lastItem, new GetFollowersObserver());
    }

    public class GetFollowersObserver extends GetListObserver {
        @Override
        public String getMessageTag() {
            return "Failed to get following: ";
        }
    }
}