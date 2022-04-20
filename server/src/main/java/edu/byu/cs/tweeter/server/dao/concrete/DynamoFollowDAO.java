package edu.byu.cs.tweeter.server.dao.concrete;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersCountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingCountResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class DynamoFollowDAO implements FollowDAO {
    private final static String REGION = "us-west-2";
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion(REGION)
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    Table followsTable = dynamoDB.getTable("follows");

    /**
     * Gets the users from the database that the user specified in the request is following. Uses
     * information in the request object to limit the number of followees returned and to return the
     * next set of followees after any that were returned in a previous request. The current
     * implementation returns generated data and doesn't actually access a database.
     *
     * @param request contains information about the user whose followees are to be returned and any
     *                other information required to satisfy the request.
     * @return the followees.
     */
    public Pair<List<String>, Boolean> getFollowees(FollowingRequest request) {
        assert request.getLimit() > 0;
        assert request.getFollowerAlias() != null;

        boolean hasMoreItems = true;
        List<String> followersAlias = new ArrayList<>();
        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":follower", request.getFollowerAlias());

        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("follower_handle = :follower")
                .withValueMap(valueMap).withScanIndexForward(true).withMaxResultSize(request.getLimit());

        if (request.getLastFolloweeAlias() != null) {
            PrimaryKey primaryKey = new PrimaryKey("follower_handle", request.getFollowerAlias(), "followee_handle", request.getLastFolloweeAlias());
            querySpec.withExclusiveStartKey(primaryKey);
        }

        try {
            QueryOutcome outcome;
            items = followsTable.query(querySpec);
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                followersAlias.add(item.getString("followee_handle"));
            }

            outcome = items.getLastLowLevelResult();
            Map<String, AttributeValue> lastKey = outcome.getQueryResult().getLastEvaluatedKey();
            if (lastKey == null) hasMoreItems = false;

        } catch (Exception e) {
            System.err.println("Unable to query");
            System.err.println(e.getMessage());
        }

        return new Pair<List<String>, Boolean>(followersAlias, hasMoreItems);
    }

    public Pair<List<String>, Boolean> getFollowers(FollowersRequest request) {
        assert request.getLimit() > 0;
        assert request.getFollowingAlias() != null;

        boolean hasMoreItems = true;
        List<String> followeesAlias = new ArrayList<>();

        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;
        QuerySpec querySpec;

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":followee", request.getFollowingAlias());
        querySpec = new QuerySpec().withKeyConditionExpression("followee_handle = :followee")
                .withValueMap(valueMap).withScanIndexForward(true).withMaxResultSize(request.getLimit());

        if (request.getLastFollowerAlias() != null) {
            PrimaryKey primaryKey = new PrimaryKey("follower_handle", request.getLastFollowerAlias(), "followee_handle", request.getFollowingAlias());
            querySpec.withExclusiveStartKey(primaryKey);
        }

        try {
            QueryOutcome outcome;
//            querySpec = new QuerySpec().withKeyConditionExpression("followee_handle = :followee")
//                    .withValueMap(valueMap).withScanIndexForward(true).withMaxResultSize(request.getLimit());

            items = followsTable.getIndex("follows_index").query(querySpec);
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                followeesAlias.add(item.getString("follower_handle"));
            }

            outcome = items.getLastLowLevelResult();
            Map<String, AttributeValue> lastKey = outcome.getQueryResult().getLastEvaluatedKey();
            if (lastKey == null) hasMoreItems = false;

        }
        catch (Exception e) {
            System.err.println("Unable to query");
            System.err.println(e.getMessage());
        }

        return new Pair<List<String>, Boolean>(followeesAlias, hasMoreItems);
    }

    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getFolloweesStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }

    @Override
    public FollowResponse follow(FollowRequest request, User currentUser) {

        try {
//            System.out.println("Adding a new item...");
            PutItemOutcome outcome = followsTable
                    .putItem(new Item().withPrimaryKey("follower_handle", currentUser.getAlias(), "followee_handle", request.getFollowee().getAlias())
                            .withString("follower_handleName", currentUser.getFirstName() + " " + currentUser.getLastName())
                            .withString("followee_handleName", request.getFollowee().getFirstName() + " " + request.getFollowee().getLastName()));
//            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            return new FollowResponse(e.getMessage());
        }
        return new FollowResponse();
    }

    @Override
    public UnfollowResponse unfollow(UnfollowRequest request, User currentUser) {
        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
                .withPrimaryKey(new PrimaryKey("follower_handle", currentUser.getAlias(), "followee_handle", request.getFollowee().getAlias()));

        try {
//            System.out.println("Attempting a conditional delete...");
            followsTable.deleteItem(deleteItemSpec);
//            System.out.println("DeleteItem succeeded");
        }
        catch (Exception e) {
            System.err.println("Unable to delete item: " + request.getFollowee());
            System.err.println(e.getMessage());
            return new UnfollowResponse(e.getMessage());
        }
        return new UnfollowResponse();
    }

    @Override
    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        assert request.getFollowee() != null;
        assert request.getFollower() != null;

        boolean isFollower = false;

        ItemCollection<QueryOutcome> items;
        Iterator<Item> iterator;
        Item item;
        QuerySpec querySpec;

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":follower", request.getFollower().getAlias());

        try {
            querySpec = new QuerySpec().withKeyConditionExpression("follower_handle = :follower")
                    .withValueMap(valueMap).withScanIndexForward(true);

            items = followsTable.query(querySpec);
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                String followee = item.getString("followee_handle");
//                System.out.println("user: " + request.getFollowee().getAlias());
                if (followee.equals(request.getFollowee().getAlias())) {
                    System.out.println("Found followed user");
                    isFollower = true;
//                    System.out.println("isFollower = " + isFollower);
                    break;
                }
            }
        }
        catch (Exception e) {
            System.err.println("Unable to query");
            System.err.println(e.getMessage());
        }
        System.out.println("isFollower = " + isFollower);
        return new IsFollowerResponse(isFollower);
    }


    public void addFollowBatch(List<List<String>> list) {

        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems(followsTable.getTableName());

        // Add each user into the TableWriteItems object
        for (List<String> strings : list) {
            Item item = new Item()
                    .withPrimaryKey("follower_handle", strings.get(0))
                    .withString("followee_handle", strings.get(1))
                    .withString("follower_name", strings.get(2))
                    .withString("followee_name", strings.get(3));
            items.addItemToPut(item);

            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems(followsTable.getTableName());
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
}
