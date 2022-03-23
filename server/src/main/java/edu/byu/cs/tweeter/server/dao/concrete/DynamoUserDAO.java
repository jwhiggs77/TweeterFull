package edu.byu.cs.tweeter.server.dao.concrete;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;

import java.util.List;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public class DynamoUserDAO implements UserDAO {
    private final static String REGION = "us-west-2";
    private final static String BUCKET_NAME = "org.higgi27.tweeter.profile.picture.bucket";
    // DynamoDB client
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion(REGION)
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    Table userTable = dynamoDB.getTable("users");
    Table authTokenTable = dynamoDB.getTable("authtoken");
    

    @Override
    public RegisterResponse register(RegisterRequest request) {
        return addNewUser(userTable, authTokenTable, request.getFirstName(), request.getLastName(), request.getUsername(), request.getPassword());
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        return getLoginUser(userTable, authTokenTable, request.getUsername());
    }

    private LoginResponse getLoginUser(Table userTable, Table authTokenTable, String username) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("alias", username);
        User currentUser = null;
        AuthToken authToken = null;
        try {
            System.out.println("Attempting to read the user item...");
            Item outcome = userTable.getItem(spec);
            currentUser = new User(outcome.get("firstName").toString(), outcome.get("lastName").toString(), outcome.get("alias").toString(), outcome.get("password").toString(), outcome.get("image").toString());
            System.out.println("GetItem succeeded: " + outcome);
            System.out.println("Attempting to read the authToken...");
            Item authOutcome = authTokenTable.getItem(spec);
            authToken = new AuthToken(String.valueOf(UUID.randomUUID()));
            System.out.println("GetItem succeeded: " + authOutcome);
        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + username);
            System.err.println(e.getMessage());
        }
        return new LoginResponse(currentUser, authToken);
    }

    private RegisterResponse addNewUser(Table userTable, Table authTokenTable, String firstName, String lastName, String username, String password) {
        String imageURL = "https://s3.us-west-2.amazonaws.com/org.higgi27.tweeter.profile.picture.bucket/profilePic";

        try {
            System.out.println("Adding a new item...");
            PutItemOutcome outcome = userTable
                    .putItem(new Item().withPrimaryKey("alias", username)
                            .withString("firstName", firstName).withString("lastName", lastName)
                            .withString("password", password).withString("image", imageURL));

            PutItemOutcome authOutcome = authTokenTable
                    .putItem(new Item().withPrimaryKey("token", String.valueOf(UUID.randomUUID())).withString("alias", username));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
            System.out.println("PutItem succeeded:\n" + authOutcome.getPutItemResult());
        }
        catch (Exception e) {
            System.err.println("Unable to add item: " + username);
            System.err.println(e.getMessage());
            return new RegisterResponse(e.getMessage());
        }
        return new RegisterResponse(firstName, lastName, username, password, imageURL);
    }

    /**
     * Create an Amazon S3 bucket.
     *
     * This code expects that you have AWS credentials set up per:
     * http://docs.aws.amazon.com/java-sdk/latest/developer-guide/setup-credentials.html
     */
    public static class CreateBucket {
        public static Bucket getBucket(String imageBucket) {
            final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(REGION).build();
            Bucket named_bucket = null;
            List<Bucket> buckets = s3.listBuckets();
            for (Bucket b : buckets) {
                if (b.getName().equals(imageBucket)) {
                    named_bucket = b;
                }
            }
            return named_bucket;
        }

        public static Bucket createBucket(String imageBucket) {
            final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(REGION).build();
            Bucket b = null;
            if (s3.doesBucketExistV2(imageBucket)) {
                System.out.format("Bucket %s already exists.\n", imageBucket);
                b = getBucket(imageBucket);
            } else {
                try {
                    b = s3.createBucket(imageBucket);
                } catch (AmazonS3Exception e) {
                    System.err.println(e.getErrorMessage());
                }
            }
            return b;
        }

        public void main(String[] args) {
            final String USAGE = "\n" +
                    "CreateBucket - create an S3 bucket\n\n" +
                    "Usage: CreateBucket " + BUCKET_NAME + "\n" +
                    "Where:\n" + BUCKET_NAME + " \n\n" +
                    "The bucket name must be unique, or an error will result.\n";

            if (args.length < 1) {
                System.out.println(USAGE);
                System.exit(1);
            }

            String imageBucket = args[0];

            System.out.format("\nCreating S3 bucket: %s\n", imageBucket);
            Bucket b = createBucket(imageBucket);
            if (b == null) {
                System.out.println("Error creating bucket!\n");
            } else {
                System.out.println("Done!\n");
            }
        }
    }
}