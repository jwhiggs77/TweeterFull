package edu.byu.cs.tweeter.server.dao.concrete;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator;
        Item item;
        QuerySpec querySpec;

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":targetUser", request.getTargetUser().getAlias());

        try {
            QueryOutcome outcome;
            querySpec = new QuerySpec().withKeyConditionExpression("receiverAlias = :targetUser")
                    .withValueMap(valueMap).withScanIndexForward(true).withMaxResultSize(request.getLimit());

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
    public Pair<ItemCollection<QueryOutcome>, Boolean> getStory(StoryRequest request) {
        assert request.getLimit() > 0;
        assert request.getTargetUser() != null;

        boolean hasMoreItems = true;
        List<Item> statusAlias = new ArrayList<>();

        ItemCollection<QueryOutcome> items = null;
        Iterator<Item> iterator;
        Item item;
        QuerySpec querySpec;

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":targetUser", request.getTargetUser().getAlias());

        try {
            QueryOutcome outcome;
            querySpec = new QuerySpec().withKeyConditionExpression("senderAlias = :targetUser")
                    .withValueMap(valueMap).withScanIndexForward(true).withMaxResultSize(request.getLimit());

            items = storyTable.query(querySpec);
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
}