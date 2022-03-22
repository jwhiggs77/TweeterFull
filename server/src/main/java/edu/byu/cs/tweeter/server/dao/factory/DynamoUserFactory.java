package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.server.dao.DynamoUserDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class DynamoUserFactory implements DAOFactory {

    @Override
    public UserDAO makeUserDAO() {
        return new DynamoUserDAO();
    }
}
