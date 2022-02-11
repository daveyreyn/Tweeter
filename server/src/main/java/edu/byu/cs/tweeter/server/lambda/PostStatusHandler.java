package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import edu.byu.cs.tweeter.model.util.JsonSerializer;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.UserService;

public class PostStatusHandler implements RequestHandler<PostStatusRequest, PostStatusResponse> {


    @Override
    public PostStatusResponse handleRequest(PostStatusRequest request, Context context) {
        String queueURL = "https://sqs.us-west-2.amazonaws.com/366989484747/SQSPostStatusQueue";
        DAOFactory daoFactory = null;
        daoFactory.setInstance(new DynamoDAOFactory());
        UserService userService = new UserService();
        PostStatusResponse response = userService.postStatus(request);
        request.getStatus().getUser().setImageBytes(null);

        if(request.getStatus().getUser().getImageBytes() == null){
            System.out.println("image bytes are null");
        }

        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(queueURL)
                .withMessageBody(JsonSerializer.serialize(request));
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);
        System.out.println(sendMessageResult.getMessageId());

        System.out.println(response.isSuccess());
        return response;
    }
}
