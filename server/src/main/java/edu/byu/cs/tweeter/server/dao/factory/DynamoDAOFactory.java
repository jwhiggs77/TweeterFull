package edu.byu.cs.tweeter.server.dao.factory;

import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.PicDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.concrete.DynamoFollowDAO;
import edu.byu.cs.tweeter.server.dao.concrete.DynamoStatusDAO;
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

    @Override
    public FollowDAO makeFollowDAO() {
        return new DynamoFollowDAO();
    }

    @Override
    public StatusDAO makeStatusDAO() {
        return new DynamoStatusDAO();
    }
}
