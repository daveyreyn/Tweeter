package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.UnfollowUserRequest;
import edu.byu.cs.tweeter.model.net.response.UnfollowUserResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;

public class UnfollowUserHandler implements RequestHandler<UnfollowUserRequest, UnfollowUserResponse> {
    @Override
    public UnfollowUserResponse handleRequest(UnfollowUserRequest request, Context context) {
        DAOFactory daoFactory = null;
        daoFactory.setInstance(new DynamoDAOFactory());

        FollowService service = new FollowService(daoFactory);

        return service.unfollowUser(request);
    }
}
