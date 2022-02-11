package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;

public class AuthTokenDAO implements AuthTokenDAOInterface {

    private static AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion("us-west-2")
            .build();
    private static DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);


    @Override
    public void addToken(String token, String timestamp) {
        Table table = dynamoDB.getTable("authtokens");

        Item item = new Item()
                .withPrimaryKey("token", token)
                .withString("time_stamp", timestamp);
        table.putItem(item);
    }

    @Override
    public String validateToken(String token) {
        Table table = dynamoDB.getTable("authtokens");
        GetItemSpec spec = new GetItemSpec().withPrimaryKey("token", token);

        Item item = table.getItem(spec);
        if (item == null) {
            return null;
        }
        return item.getString("time_stamp");
    }

    @Override
    public void deleteToken(String token) {
        Table table = dynamoDB.getTable("authtokens");
        table.deleteItem("token", token);
    }
}
