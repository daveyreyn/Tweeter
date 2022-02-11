package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.model.net.request.BatchShareStatusRequest;
import edu.byu.cs.tweeter.model.util.JsonSerializer;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.FeedService;


public class FeedQueueHandler  implements RequestHandler<SQSEvent, Void> {
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        DAOFactory daoFactory = null;
        daoFactory.setInstance(new DynamoDAOFactory());
        FeedService service = new FeedService(daoFactory);
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            System.out.println(msg.getBody());
            BatchShareStatusRequest body = JsonSerializer.deserialize(msg.getBody(), BatchShareStatusRequest.class);
            service.batchFeedUpdate(body);
        }

        return null;
    }
}
