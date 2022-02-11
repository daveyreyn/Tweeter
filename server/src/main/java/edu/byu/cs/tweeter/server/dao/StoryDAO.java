package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.util.Pair;
import edu.byu.cs.tweeter.model.domain.User;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

public class StoryDAO implements StoryDAOInterface {

    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);

    @Override
    public Pair<List<Status>, Boolean> getStory(String alias, String timeStamp, int pageSize){
        Map<String, String> attrNames = new HashMap<>();
        attrNames.put("#handle", "alias");
        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":alias", new AttributeValue().withS(alias));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName("stories")
                .withKeyConditionExpression("#handle = :alias")
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues)
                .withLimit(pageSize);

        if (timeStamp != null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("alias", new AttributeValue().withS(alias));
            startKey.put("time_stamp", new AttributeValue().withN(timeStamp));

            queryRequest = queryRequest.withExclusiveStartKey(startKey);
        }

        List<Status> statuses = new ArrayList<>();

        QueryResult queryResult = amazonDynamoDB.query(queryRequest);

        List<Map<String, AttributeValue>> items = queryResult.getItems();
        if (items != null) {
            for (Map<String, AttributeValue> item: items) {
                String currAlias = item.get("alias").getS();
                String timestamp = item.get("time_stamp").getS();
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
                System.out.println(mentions);
                System.out.println(urls);


                User user = new User(first_name, last_name, currAlias, image_url);
                statuses.add(new Status(content, user, timestamp, urls, mentions));
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
}
