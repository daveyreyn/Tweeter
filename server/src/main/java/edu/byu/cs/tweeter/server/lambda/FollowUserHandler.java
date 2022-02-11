package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowUserRequest;
import edu.byu.cs.tweeter.model.net.response.FollowUserResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.server.service.UserService;

public class FollowUserHandler implements RequestHandler<FollowUserRequest, FollowUserResponse> {
    @Override
    public FollowUserResponse handleRequest(FollowUserRequest request, Context context) {
        DAOFactory daoFactory = null;
        daoFactory.setInstance(new DynamoDAOFactory());

        FollowService service = new FollowService(daoFactory);

        return service.followUser(request);
    }
}
