package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.BatchShareStatusRequest;
import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.model.util.JsonSerializer;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class StatusQueueHandler implements RequestHandler<SQSEvent, Void>  {


    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        String queueURL = "https://sqs.us-west-2.amazonaws.com/366989484747/SQSUpdateFeedQueue";
        DAOFactory daoFactory = null;
        daoFactory.setInstance(new DynamoDAOFactory());

        FollowService service = new FollowService(daoFactory);

        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            System.out.println(msg.getBody());
            PostStatusRequest body = JsonSerializer.deserialize(msg.getBody(), PostStatusRequest.class);
            String lastFollowerAlias = null;
            AuthToken authToken = body.getAuthToken();
            User author = body.getStatus().getUser();
            while (true) {
                FollowerRequest request = new FollowerRequest(authToken, author.getAlias(), 1000, lastFollowerAlias);
                FollowerResponse response = service.getFollowers(request);
                if (!response.isSuccess()) {
                    throw new RuntimeException("500");
                }

                BatchShareStatusRequest batch = new BatchShareStatusRequest(response.getFollowers(), body.getStatus(), authToken);
                SendMessageRequest sendMessageRequest = new SendMessageRequest()
                        .withQueueUrl(queueURL)
                        .withMessageBody(JsonSerializer.serialize(batch));
                AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
                SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);
                System.out.println(sendMessageResult.getMessageId());

                if (!response.getHasMorePages()) {
                    break;
                }
                try {
                    lastFollowerAlias = response.getFollowers().get(999).getAlias();
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                    break;
                }
            }


        }
        return null;
    }
}
