package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.server.dao.UserDAO;

public interface DAOFactory {
    UserDAO makeUserDAO();
}
