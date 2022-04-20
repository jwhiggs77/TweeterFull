package edu.byu.cs.tweeter.server.dao.concrete;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
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
        return getLoginUser(userTable, authTokenTable, request.getUsername(), request.getPassword());
    }

    @Override
    public UserResponse getUser(UserRequest request) {
        return getItem(userTable, request.getAlias());
    }

    @Override
    public User getUserFromToken(AuthToken authToken) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("token", authToken.token);
        Item authTokenTableItem = authTokenTable.getItem(spec);

        String alias = authTokenTableItem.getString("alias");

        return getItem(userTable, alias).getUser();
    }

    @Override
    public void updateFollowCount(User currentUser, Integer count, boolean iterate, String countType) {
        UpdateItemSpec updateItemSpec = getUpdateItemSpec(currentUser, count, iterate, countType);

        try {
//            System.out.println("Updating the item...");
            UpdateItemOutcome outcome = userTable.updateItem(updateItemSpec);
//            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());
        }
        catch (Exception e) {
            System.err.println("Unable to update count for: " + currentUser.getAlias());
            System.err.println(e.getMessage());
        }
    }

    @Override
    public FollowersCountResponse getFollowerCount(User targetUser) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("alias", targetUser.getAlias());
        Item outcome = userTable.getItem(spec);
        return new FollowersCountResponse(outcome.getInt("followerCount"));
    }

    @Override
    public FollowingCountResponse getFolloweeCount(User targetUser) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("alias", targetUser.getAlias());
        Item outcome = userTable.getItem(spec);
        return new FollowingCountResponse(outcome.getInt("followingCount"));
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey("token", request.getAuthToken().getToken()));

        try {
//            System.out.println("Attempting a conditional delete...");
            authTokenTable.deleteItem(deleteItemSpec);
//            System.out.println("DeleteItem succeeded");
        }
        catch (Exception e) {
            System.err.println("Unable to delete authToken");
            System.err.println(e.getMessage());
            return new LogoutResponse(e.getMessage());
        }
        return new LogoutResponse();
    }

    private UpdateItemSpec getUpdateItemSpec(User currentUser, Integer count, boolean iterate, String countType) {
        if (iterate) count++; else count--;
        return new UpdateItemSpec().withPrimaryKey("alias", currentUser.getAlias())
                .withUpdateExpression("set " + countType + " = :r")
                .withValueMap(new ValueMap().withInt(":r", count))
                .withReturnValues(ReturnValue.UPDATED_NEW);
    }

    private static UserResponse getItem(Table userTable, String username) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("alias", username);
        User retrievedUser = null;
        try {
//            System.out.println("Attempting to read the item...");
            Item outcome = userTable.getItem(spec);
//            System.out.println("GetItem succeeded: " + outcome);
            retrievedUser = new User(outcome.getString("firstName"), outcome.getString("lastName"), outcome.getString("alias"), outcome.getString("password"), outcome.getString("image"));
        }
        catch (Exception e) {
//            System.err.println("Unable to read item: " + follower_handle + " " + followee_handle);
            System.err.println(e.getMessage());
        }
        return new UserResponse(retrievedUser);
    }

    private LoginResponse getLoginUser(Table userTable, Table authTokenTable, String username, String password) {
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("alias", username);
        User currentUser = null;
        AuthToken authToken = null;
        try {
//            System.out.println("Attempting to read the user item...");
            Item outcome = userTable.getItem(spec);
            if (outcome == null) return new LoginResponse("User does not exist");
            String userPassword = getSecurePassword(password, outcome.getString("salt"));
//            System.out.println(userPassword + " --- " + outcome.getString("password"));
            if (!userPassword.equals(outcome.getString("password"))) return new LoginResponse("Incorrect password");
            currentUser = new User(outcome.get("firstName").toString(), outcome.get("lastName").toString(), outcome.get("alias").toString(), outcome.get("password").toString(), outcome.get("image").toString());
//            System.out.println("GetItem succeeded: " + outcome);
            authToken = new AuthToken(String.valueOf(UUID.randomUUID()));
            authTokenTable.putItem(new Item().withPrimaryKey("token", authToken.getToken()).withString("alias", username).withString("timeStamp", authToken.getDatetime()));
        }
        catch (Exception e) {
            System.err.println("Unable to read item: " + username);
            System.err.println(e.getMessage());
        }
        return new LoginResponse(currentUser, authToken);
    }

    private RegisterResponse addNewUser(Table userTable, Table authTokenTable, String firstName, String lastName, String username, String password) {
        String imageURL = "https://s3.us-west-2.amazonaws.com/org.higgi27.tweeter.profile.picture.bucket/%40" + username.substring(1, username.length());

        AuthToken authToken;
        try {
//            System.out.println("Adding a new item...");
            String salt = getSalt();
            password = getSecurePassword(password, salt);
            PutItemOutcome outcome = userTable
                    .putItem(new Item().withPrimaryKey("alias", username)
                            .withString("firstName", firstName).withString("lastName", lastName)
                            .withString("password", password).withString("image", imageURL).withInt("followerCount", 0).withInt("followingCount", 0).withString("salt", salt));

            authToken = new AuthToken(String.valueOf(UUID.randomUUID()));
            PutItemOutcome authOutcome = authTokenTable
                    .putItem(new Item().withPrimaryKey("token", authToken.getToken()).withString("alias", username).withString("timeStamp", authToken.getDatetime()));

//            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
        }
        catch (Exception e) {
            System.err.println("Unable to add item: " + username);
            System.err.println(e.getMessage());
            return new RegisterResponse(e.getMessage());
        }
        return new RegisterResponse(firstName, lastName, username, password, imageURL, authToken);
    }

    private static String getSecurePassword(String passwordToHash, String salt) {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Add password bytes to digest
            md.update(salt.getBytes());

            // Get the hash's bytes
            byte[] bytes = md.digest(passwordToHash.getBytes());

            // This bytes[] has bytes in decimal format;
            // Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < bytes.length; i++) {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16)
                        .substring(1));
            }

            // Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    private static String getSalt() throws NoSuchAlgorithmException, NoSuchProviderException {
        // Always use a SecureRandom generator
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "SUN");

        // Create array for salt
        byte[] salt = new byte[16];

        // Get a random salt
        sr.nextBytes(salt);

        // return salt
        return salt.toString();
    }

    @Override
    public void addUserBatch(List<User> users) {

        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems(userTable.getTableName());

        // Add each user into the TableWriteItems object
        for (User user : users) {
            Item item = new Item()
                    .withPrimaryKey("alias", user.getAlias())
                    .withString("firstName", user.getFirstName())
                    .withString("lastName", user.getLastName())
                    .withString("image", user.getImageUrl())
                    .withInt("followerCount", 0)
                    .withInt("followeeCount", 0);
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems(userTable.getTableName());
            }
        }

        // Write any leftover items
        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items);
        }
    }

    private void loopBatchWrite(TableWriteItems items) {

        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);
        System.out.println(outcome);
//        logger.log("Wrote User Batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
            System.out.println(outcome);
        }
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
