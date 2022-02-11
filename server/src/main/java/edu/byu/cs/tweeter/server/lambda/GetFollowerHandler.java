package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowerRequest;
import edu.byu.cs.tweeter.model.net.response.FollowerResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowerHandler implements RequestHandler<FollowerRequest, FollowerResponse> {
    @Override
    public FollowerResponse handleRequest(FollowerRequest request, Context context) {
        DAOFactory daoFactory = null;
        daoFactory.setInstance(new DynamoDAOFactory());

        FollowService service = new FollowService(daoFactory);
        return service.getFollowers(request);
    }
}

