package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.PicDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public interface DAOFactory {
    UserDAO makeUserDAO();
    PicDAO makePicDAO();
    FollowDAO makeFollowDAO();
    StatusDAO makeStatusDAO();

}
