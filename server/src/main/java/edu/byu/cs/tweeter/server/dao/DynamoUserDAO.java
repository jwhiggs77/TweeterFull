package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;

import java.util.List;
import java.util.UUID;

public class DynamoUserDAO implements UserDAO {
    private final static String REGION = "us-west-2";
    private final static String BUCKET_NAME = "org.higgi27.tweeter.profile.picture.bucket";
    // DynamoDB client
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion(REGION)
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    Table table = dynamoDB.getTable("users");

    @Override
    public RegisterResponse register(String firstName, String lastName, String username, String password, String image) {
        return addItem(table, firstName, lastName, username, password, image);
    }

    private static RegisterResponse addItem(Table table, String firstName, String lastName, String username, String password, String image) {
        String imageURL = null;
        try {
            CreateBucket.createBucket(BUCKET_NAME);

            AmazonS3 s3client = AmazonS3ClientBuilder
                    .standard().withRegion(REGION).build();

            s3client.putObject(BUCKET_NAME, "profilePic", image);
            imageURL = s3client.getObjectAsString(BUCKET_NAME, "profilePic")

            System.out.println("Adding a new item...");
            PutItemOutcome outcome = table
                    .putItem(new Item().withPrimaryKey("alias", username)
                            .withString("authtoken", String.valueOf(UUID.randomUUID())).withString("firstName", firstName).withString("lastName", lastName)
                            .withString("password", password).withString("image", imageURL));

            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
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
