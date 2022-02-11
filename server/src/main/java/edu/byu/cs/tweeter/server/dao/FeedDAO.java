package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.util.Pair;

public class FeedDAO implements FeedDAOInterface{

    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);

    @Override
    public Pair<List<Status>, Boolean> getFeed(String alias, int pageSize, String timestamp) {

        Map<String, String> attrNames = new HashMap<>();
        attrNames.put("#handle", "alias");

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":alias", new AttributeValue().withS(alias));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName("feed")
                .withKeyConditionExpression("#handle = :alias")
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues)
                .withLimit(pageSize);

        if (timestamp != null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("alias", new AttributeValue().withS(alias));
            startKey.put("time_stamp", new AttributeValue().withN(String.valueOf(timestamp)));

            queryRequest = queryRequest.withExclusiveStartKey(startKey);
        }

        List<Status> statuses = new ArrayList<>();
        QueryResult queryResult = amazonDynamoDB.query(queryRequest);
        List<Map<String, AttributeValue>> items = queryResult.getItems();
        if (items != null) {
            for (Map<String, AttributeValue> item: items) {
                String currAlias = item.get("author").getS();
                String currTimestamp = item.get("time_stamp").getS();
                String content = item.get("content").getS();
                String first_name = item.get("first_name").getS();
                String last_name = item.get("last_name").getS();
                String image_url = item.get("image_url").getS();

                List<AttributeValue> aMentions = item.get("mentions").getL();
                List<String> mentions = new ArrayList<>();
                for(AttributeValue a: aMentions){
                    mentions.add(a.getS());
                }
                List<AttributeValue> aUrls = item.get("urls").getL();
                List<String> urls = new ArrayList<>();
                for(AttributeValue a: aUrls){
                    urls.add(a.getS());
                }

                User author = new User(first_name, last_name, currAlias, image_url);
                statuses.add(new Status(content, author, currTimestamp, urls, mentions));
            }
        }


        boolean hasMorePages = false;
        Map<String, AttributeValue> lastKey = queryResult.getLastEvaluatedKey();
        if (lastKey != null) {
            hasMorePages = true;
        }

        for (int i = 0, j = statuses.size() - 1; i < j; i++) {
            statuses.add(i, statuses.remove(j));
        }
        return new Pair<>(statuses, hasMorePages);
    }
    @Override
    public void putFeed(Status status, List<User> followers){

        Table table = dynamoDB.getTable("feed");
        try {
            for(User user : followers){
                Item item = new Item()
                        .withPrimaryKey("alias", user.getAlias(), "time_stamp", status.getDate())
                        .withString("first_name", status.getUser().getFirstName())
                        .withString("last_name", status.getUser().getLastName())
                        .withString("author", status.getUser().getAlias())
                        .withString("image_url", status.getUser().getImageUrl())
                        .withString("content", status.getPost())
                        .withList("urls",  status.getUrls())
                        .withList("mentions", status.getMentions());
                table.putItem(item);

            }

        }
        catch (Exception e){

        }

    }

    @Override
    public void batchShareStatus(Status status, List<User> followers) {
        //List<Item> items = new ArrayList<>();
        TableWriteItems items = new TableWriteItems("feed");

        for (User user : followers) {
            Item item = new Item()
                    .withPrimaryKey("alias", user.getAlias(), "time_stamp", status.getDate())
                    .withString("first_name", status.getUser().getFirstName())
                    .withString("last_name", status.getUser().getLastName())
                    .withString("author", status.getUser().getAlias())
                    .withString("image_url", status.getUser().getImageUrl())
                    .withString("content", status.getPost())
                    .withList("urls",  status.getUrls())
                    .withList("mentions", status.getMentions());
            items.addItemToPut(item);
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems("feed");
            }
        }

        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items);
        }

    }

    private void loopBatchWrite(TableWriteItems items) {

        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
        }
    }

    /*

    @Override
    public void batchShareStatus(Status status, List<User> followers) {
        List<Item> items = new ArrayList<>();
        for (User user : followers) {
            Item item = new Item()
                    .withPrimaryKey("alias", user.getAlias(), "time_stamp", status.getDate())
                    .withString("first_name", status.getUser().getFirstName())
                    .withString("last_name", status.getUser().getLastName())
                    .withString("author", status.getUser().getAlias())
                    .withString("image_url", status.getUser().getImageUrl())
                    .withString("content", status.getPost())
                    .withList("urls",  status.getUrls())
                    .withList("mentions", status.getMentions());
            items.add(item);
        }

        //rewrite any that didn't get written like in the example exercise
        TableWriteItems forumTableWriteItems = new TableWriteItems("feed")
                .withItemsToPut(items);

        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(forumTableWriteItems);
    } */

}
