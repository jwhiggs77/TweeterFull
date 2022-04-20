package edu.byu.cs.tweeter.server.dao.concrete;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.server.dao.PicDAO;

public class s3DAO implements PicDAO {
    private final static String REGION = "us-west-2";
    private final static String BUCKET_NAME = "org.higgi27.tweeter.profile.picture.bucket";

    @Override
    public void upload(String image, String username) {
        byte[] profilePic = Base64.getDecoder().decode(image);
        InputStream inputStream = new ByteArrayInputStream(profilePic);
        ObjectMetadata data = new ObjectMetadata();
        data.setContentLength(profilePic.length);
        data.setContentType("image/jpeg");
        DynamoUserDAO.CreateBucket.createBucket(BUCKET_NAME);

        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard().withRegion(REGION).build();

        s3client.putObject(BUCKET_NAME, username, inputStream, data);
    }
}
