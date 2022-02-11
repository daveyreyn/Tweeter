package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.util.Pair;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.*;

import java.util.List;
import java.util.Map;


public class UserDAO implements UserDAOInterface {

    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);



    @Override
    public User getUser(String alias){

        Table table = dynamoDB.getTable("users");
        Item item = table.getItem("alias", alias);
        if (item == null) {
            return null;
        }

        String userAlias = item.getString("alias");
        String first_name = item.getString("first_name");
        String last_name = item.getString("last_name");
        String image_url = item.getString("image_url");

        return new User(first_name, last_name, userAlias, image_url);
    }
    @Override
    public void updateFollowing(String alias, boolean follow){
        Table table = dynamoDB.getTable("users");
        Item item = table.getItem("alias", alias );

        int currNumFollowing = item.getInt("following");
        if(follow){
            currNumFollowing++;
        }
        else {
            currNumFollowing--;
        }
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("alias", alias)
                .withUpdateExpression("set following = :f").withValueMap(new ValueMap().withNumber(":f", currNumFollowing)).
                        withReturnValues(ReturnValue.UPDATED_NEW);

        try {
            System.out.println("Updating the item...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }
    @Override
    public void updateBatchFollowers(String alias, int numFollowers){
        Table table = dynamoDB.getTable("users");
        Item item = table.getItem("alias", alias );
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("alias", alias)
                .withUpdateExpression("set followers = :f").withValueMap(new ValueMap().withNumber(":f", numFollowers)).
                        withReturnValues(ReturnValue.UPDATED_NEW);

        try {
            System.out.println("Updating the item...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void resetFollowers(String alias){
        Table table = dynamoDB.getTable("users");
        Item item = table.getItem("alias", alias );
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("alias", alias)
                .withUpdateExpression("set followers = :f").withValueMap(new ValueMap().withNumber(":f", 0)).
                        withReturnValues(ReturnValue.UPDATED_NEW);

        try {
            System.out.println("Updating the item...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }


    //method on the dao that returns the hashed password given alias
    @Override
    public void updateFollowers(String alias, boolean follow){
        Table table = dynamoDB.getTable("users");
        Item item = table.getItem("alias", alias );

        int currNumFollowers = item.getInt("followers");
        if(follow){
            currNumFollowers++;
        }
        else {
            currNumFollowers--;
        }
        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("alias", alias)
                .withUpdateExpression("set followers = :f").withValueMap(new ValueMap().withNumber(":f", currNumFollowers)).
                        withReturnValues(ReturnValue.UPDATED_NEW);

        try {
            System.out.println("Updating the item...");
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());

        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    @Override
    public int getNumFollowing(String alias){
        Table table = dynamoDB.getTable("users");
        Item item = table.getItem("alias", alias );
        if (item == null) {
            System.out.println("Alias not found when getting num following");
        }
        int currNumFollowing = item.getInt("following");

        return currNumFollowing;
    }

    @Override
    public int getNumFollowers(String alias){
        Table table = dynamoDB.getTable("users");
        Item item = table.getItem("alias", alias );
        if (item == null) {
            System.out.println("Alias not found when getting num followers");
        }
        int currNumFollowers = item.getInt("followers");

        return currNumFollowers;
    }

    @Override
    public Pair<User, String> loginUser(String alias) {
        Table table = dynamoDB.getTable("users");
        Item item = table.getItem("alias", alias);
        if (item == null) {
            return null;
        }
        String userAlias = item.getString("alias");
        String firstName = item.getString("first_name");
        String lastName = item.getString("last_name");
        String image_url = item.getString("image_url");
        String userPassword = item.getString("password");

        User user = new User(firstName, lastName, userAlias, image_url);
        return new Pair<>(user, userPassword);
    }



    @Override
    public String postStatus(String alias, String firstName, String lastName, String imageURL,
                                         String content, String timestamp, List<String> urls, List<String> mentions) {
        Table table = dynamoDB.getTable("stories");

        try {
            Item item = new Item()
                    .withPrimaryKey("alias", alias, "time_stamp", timestamp)
                    .withString("first_name", firstName)
                    .withString("last_name", lastName)
                    .withString("image_url", imageURL)
                    .withString("content", content)
                    .withList("urls",  urls)
                    .withList("mentions", mentions);
            table.putItem(item);
        }
        catch (Exception e){

            return e.getMessage();
        }
        return null;
    }


    @Override
    public User registerUser(String alias, String firstName, String lastName,
                                         String password, String imageURL) {

        Table table = dynamoDB.getTable("users");

        Item check = table.getItem("alias", alias);
        if (check != null) {
            return null;
        }

        Item item = new Item()
                .withPrimaryKey("alias", alias)
                .withString("first_name", firstName)
                .withString("last_name", lastName)
                .withString("image_url", imageURL)
                .withString("password", password)
                .withInt("followers", 0)
                .withInt("following", 0);
        table.putItem(item);

        User user = new User(firstName, lastName, alias, imageURL);
        return user;

    }


    @Override
    public void addUserBatch(List<User> users) {
        TableWriteItems items = new TableWriteItems("users");
        for (User user : users) {
            Item item = new Item()
                    .withPrimaryKey("alias", user.getAlias())
                    .withString("first_name", user.getFirstName())
                    .withString("last_name", user.getLastName())
                    .withString("image_url", user.getImageUrl())
                    .withString("password", "1234")
                    .withInt("followers", 0)
                    .withInt("following", 0);
            items.addItemToPut(item);


            if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                loopBatchWrite(items);
                items = new TableWriteItems("users");
            }
        }
        if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
            loopBatchWrite(items);
        }
    }

    private void loopBatchWrite(TableWriteItems items) {

        // The 'dynamoDB' object is of type DynamoDB and is declared statically in this example
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);
        //logger.log("Wrote User Batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
            //logger.log("Wrote more Users");
        }
    }


}
