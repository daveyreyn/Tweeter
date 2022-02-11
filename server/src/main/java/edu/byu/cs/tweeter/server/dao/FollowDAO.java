package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.util.Pair;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;


public class FollowDAO implements FollowDAOInterface {

    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);


    @Override
    public String followUser(String followerAlias, String followeeAlias, String followerFirstName,
                                         String followerLastName, String followerImageURL, String followeeFirstName,
                                         String followeeLastName, String followeeImageURL) {

        Table table = dynamoDB.getTable("follows");
        Item item = new Item()
                .withPrimaryKey("follower_handle", followerAlias, "followee_handle", followeeAlias)
                .withString("follower_first_name", followerFirstName)
                .withString("follower_last_name", followerLastName)
                .withString("follower_image_url", followerImageURL)
                .withString("followee_first_name", followeeFirstName)
                .withString("followee_last_name", followeeLastName)
                .withString("followee_image_url",followeeImageURL);
        table.putItem(item);
        return null;
    }

    @Override
    public void addFollowBatch(List<Pair<User, User>> pairList) {
        TableWriteItems items = new TableWriteItems("follows");
        for (Pair<User, User> userPair : pairList) {
            Item item = new Item()
                    .withPrimaryKey("follower_handle", userPair.getFirst().getAlias(), "followee_handle", userPair.getSecond().getAlias())
                    .withString("follower_first_name", userPair.getFirst().getFirstName())
                    .withString("follower_last_name", userPair.getFirst().getLastName())
                    .withString("follower_image_url", userPair.getFirst().getImageUrl())
                    .withString("followee_first_name", userPair.getSecond().getFirstName())
                    .withString("followee_last_name", userPair.getSecond().getLastName())
                    .withString("followee_image_url",userPair.getSecond().getImageUrl());

            items.addItemToPut(item);
            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems("follows");
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

    @Override
    public String unfollowUser(String followerAlias, String followeeAlias){
        Table table = dynamoDB.getTable("follows");
        table.deleteItem("follower_handle", followerAlias, "followee_handle", followeeAlias);
        return null;
    }

    @Override
    public Pair<List<User>, Boolean> getFollowees(String user, String lastFolloweeAlias, int pageSize) {
        assert pageSize > 0;
        assert user != null;

        Map<String, String> attrNames = new HashMap<>();
        attrNames.put("#follower", "follower_handle");

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":follower_handle", new AttributeValue().withS(user));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName("follows")
                .withKeyConditionExpression("#follower = :follower_handle")
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues)
                .withLimit(pageSize);

        if (lastFolloweeAlias != null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("follower_handle", new AttributeValue().withS(user));
            startKey.put("followee_handle", new AttributeValue().withS(lastFolloweeAlias));

            queryRequest = queryRequest.withExclusiveStartKey(startKey);
        }

        List<User> followees = new ArrayList<>();
        QueryResult queryResult = amazonDynamoDB.query(queryRequest);
        List<Map<String, AttributeValue>> items = queryResult.getItems();
        if (items != null) {
            for (Map<String, AttributeValue> item: items) {
                String alias = item.get("followee_handle").getS();
                String first_name = item.get("followee_first_name").getS();
                String last_name = item.get("followee_last_name").getS();
                String image_url = item.get("followee_image_url").getS();
                followees.add(new User(first_name, last_name, alias, image_url));
            }
        }
        boolean hasMorePages = false;
        Map<String, AttributeValue> lastKey = queryResult.getLastEvaluatedKey();
        if (lastKey != null) {
            hasMorePages = true;
        }

        return new Pair<>(followees, hasMorePages);

    }


    @Override
    public Pair<List<User>, Boolean>  getFollowers(String followeeAlias, String lastFollowerAlias, int pageSize) {

        assert pageSize > 0;
        assert followeeAlias != null;
        Map<String, String> attrNames = new HashMap<>();
        attrNames.put("#followee", "followee_handle");

        Map<String, AttributeValue> attrValues = new HashMap<>();
        attrValues.put(":followee_handle", new AttributeValue().withS(followeeAlias));

        QueryRequest queryRequest = new QueryRequest()
                .withTableName("follows")
                .withIndexName("followee_handle-follower_handle-index")
                .withKeyConditionExpression("#followee = :followee_handle")
                .withExpressionAttributeNames(attrNames)
                .withExpressionAttributeValues(attrValues)
                .withLimit(pageSize);

        if (lastFollowerAlias != null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("followee_handle", new AttributeValue().withS(followeeAlias));
            startKey.put("follower_handle", new AttributeValue().withS(lastFollowerAlias));

            queryRequest = queryRequest.withExclusiveStartKey(startKey);
        }

        List<User> followers = new ArrayList<>();
        QueryResult queryResult = amazonDynamoDB.query(queryRequest);

        List<Map<String, AttributeValue>> items = queryResult.getItems();
        if (items != null) {
            for (Map<String, AttributeValue> item: items) {
                String alias = item.get("follower_handle").getS();
                String first_name = item.get("follower_first_name").getS();
                String last_name = item.get("follower_last_name").getS();
                String image_url = item.get("follower_image_url").getS();
                followers.add(new User(first_name, last_name, alias, image_url));
            }
        }

        boolean hasMorePages = false;
        Map<String, AttributeValue> lastKey = queryResult.getLastEvaluatedKey();
        if (lastKey != null) {
            hasMorePages = true;
        }
        return new Pair<>(followers, hasMorePages);
    }

    @Override
    public Boolean checkFollowStatus(String followerAlias, String followeeAlias){
        Table table = dynamoDB.getTable("follows");
        boolean isFollower = false;
        Item item = table.getItem("follower_handle", followerAlias, "followee_handle", followeeAlias);
        if (item != null) {
            isFollower = true;
        }
        System.out.println(followerAlias + " is followerAlias");
        System.out.println(followeeAlias + " is followeeAlias");
        System.out.println(isFollower);
        return isFollower;
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

    private int getFollowersStartingIndex(String lastFollowerAlias, List<User> allFollowers) {

        int followersIndex = 0;

        if(lastFollowerAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowers.size(); i++) {
                if(lastFollowerAlias.equals(allFollowers.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followersIndex = i + 1;
                    break;
                }
            }
        }

        return followersIndex;
    }



}
