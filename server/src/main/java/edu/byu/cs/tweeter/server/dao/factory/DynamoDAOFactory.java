package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.server.dao.PicDAO;
import edu.byu.cs.tweeter.server.dao.concrete.DynamoUserDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.concrete.s3DAO;

public class DynamoDAOFactory implements DAOFactory {

    @Override
    public UserDAO makeUserDAO() {
        return new DynamoUserDAO();
    }

    @Override
    public PicDAO makePicDAO() {
        return new s3DAO();
    }
}
