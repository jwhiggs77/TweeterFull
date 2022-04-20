package edu.byu.cs.tweeter.server.dao.concrete;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.DTO.PostStatusDTO;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.util.Pair;

public class DynamoStatusDAO implements StatusDAO {
    private final static String REGION = "us-west-2";
    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion(REGION)
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
    Table feedTable = dynamoDB.getTable("feed");
    Table storyTable = dynamoDB.getTable("story");

    @Override
    public Pair<ItemCollection<QueryOutcome>, Boolean> getFeed(FeedRequest request) {
        assert request.getLimit() > 0;
        assert request.getTargetUser() != null;

        boolean hasMoreItems = true;
        List<Item> statusAlias = new ArrayList<>();
        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":targetUser", request.getTargetUser().getAlias());

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator;
        Item item;
        QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("receiverAlias = :targetUser")
                .withValueMap(valueMap).withScanIndexForward(true).withMaxResultSize(request.getLimit());

        if (request.getLastStatus() != null) {
            PrimaryKey primaryKey = new PrimaryKey("receiverAlias", request.getLastStatus().getUser().getAlias(), "timeStamp", request.getLastStatus().getDate());
            querySpec.withExclusiveStartKey(primaryKey);
        }

        try {
            QueryOutcome outcome;
            items = feedTable.query(querySpec);
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                statusAlias.add(item);
            }

            outcome = items.getLastLowLevelResult();
            Map<String, AttributeValue> lastKey = outcome.getQueryResult().getLastEvaluatedKey();
            if (lastKey == null) hasMoreItems = false;

        }
        catch (Exception e) {
            System.err.println("Unable to query");
            System.err.println(e.getMessage());
        }

        return new Pair<ItemCollection<QueryOutcome>, Boolean>(items, hasMoreItems);
    }

    @Override
    public Pair<List<Status>, Boolean> getStory(StoryRequest request) {
        assert request.getLimit() > 0;
        assert request.getTargetUser() != null;

        boolean hasMoreItems = true;
        List<Status> statusAlias = new ArrayList<>();

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator;
        Item item;
        QuerySpec querySpec;

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":targetUser", request.getTargetUser().getAlias());
        querySpec = new QuerySpec().withKeyConditionExpression("senderAlias = :targetUser")
                .withValueMap(valueMap).withScanIndexForward(true).withMaxResultSize(request.getLimit());

        if (request.getLastStatus() != null) {
            PrimaryKey primaryKey = new PrimaryKey("senderAlias", request.getLastStatus().getUser().getAlias(), "timeStamp", request.getLastStatus().getDate());
            querySpec.withExclusiveStartKey(primaryKey);
        }

        try {
            QueryOutcome outcome;

            items = storyTable.query(querySpec);
            iterator = items.iterator();
            while (iterator.hasNext()) {
                item = iterator.next();
                User newUser = new User(null, null, item.getString("senderAlias"), null, null);
                statusAlias.add(new Status(item.getString("post"), newUser, item.getString("timeStamp"),
                        item.getList("urls"), item.getList("mentions")));
            }

            outcome = items.getLastLowLevelResult();
            Map<String, AttributeValue> lastKey = outcome.getQueryResult().getLastEvaluatedKey();
            if (lastKey == null) hasMoreItems = false;

        }
        catch (Exception e) {
            System.err.println("Unable to query");
            System.err.println(e.getMessage());
        }

        return new Pair<List<Status>, Boolean>(statusAlias, hasMoreItems);
    }

    @Override
    public PostStatusResponse postStatus(PostStatusRequest request, List<String> followers) {
        storyTable.putItem(new Item().withPrimaryKey("senderAlias", request.getStatus().getUser().getAlias(), "timeStamp", request.getStatus().getDate())
                        .withString("post", request.getStatus().getPost()).withList("mentions", request.getStatus().getMentions()).withList("urls", request.getStatus().getUrls()));
        for (String alias : followers) {
            feedTable.putItem(new Item().withPrimaryKey("receiverAlias", alias, "timeStamp", request.getStatus().getDate())
                    .withString("post", request.getStatus().getPost()).withString("senderAlias", request.getStatus().getUser().getAlias())
                    .withList("mentions", request.getStatus().getMentions()).withList("urls", request.getStatus().getUrls()));
        }

        return new PostStatusResponse(request.getStatus());
    }

    @Override
    public boolean postStory(PostStatusRequest request) {
        storyTable.putItem(new Item().withPrimaryKey("senderAlias", request.getStatus().getUser().getAlias(), "timeStamp", request.getStatus().getDate())
                .withString("post", request.getStatus().getPost()).withList("mentions", request.getStatus().getMentions()).withList("urls", request.getStatus().getUrls()));

        return true;
    }

    @Override
    public void addStatusBatch(List<PostStatusDTO> statusBatch) {

        // Constructor for TableWriteItems takes the name of the table, which I have stored in TABLE_USER
        TableWriteItems items = new TableWriteItems(feedTable.getTableName());

        // Add each user into the TableWriteItems object
        for (PostStatusDTO dto : statusBatch) {
            Item item = new Item().withPrimaryKey("receiverAlias", dto.getAlias(), "timeStamp", dto.getStatus().getDate())
                    .withString("post", dto.getStatus().getPost()).withString("senderAlias", dto.getStatus().getUser().getAlias())
                    .withList("mentions", dto.getStatus().getMentions()).withList("urls", dto.getStatus().getUrls());
            items.addItemToPut(item);
            // 25 is the maximum number of items allowed in a single batch write.
            // Attempting to write more than 25 items will result in an exception being thrown
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems(feedTable.getTableName());
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
//        logger.log("Wrote User Batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
//            logger.log("Wrote more Users");
        }
    }
}