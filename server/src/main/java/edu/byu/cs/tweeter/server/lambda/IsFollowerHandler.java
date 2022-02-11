package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.UserService;

public class IsFollowerHandler implements RequestHandler<IsFollowerRequest, IsFollowerResponse> {

    @Override
    public IsFollowerResponse handleRequest(IsFollowerRequest request, Context context) {
        DAOFactory daoFactory = null;
        daoFactory.setInstance(new DynamoDAOFactory());
        UserService service = new UserService();
        return service.isFollower(request);

    }
}
