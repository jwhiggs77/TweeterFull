package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.byu.cs.tweeter.DTO.PostStatusDTO;
import edu.byu.cs.tweeter.model.net.JsonSerializer;
import edu.byu.cs.tweeter.server.dao.factory.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.StatusService;

public class UpdateFeeds implements RequestHandler<SQSEvent, Void> {
    StatusService statusService = new StatusService(new DynamoDAOFactory());

    @Override
    public Void handleRequest(SQSEvent input, Context context) {

        Iterator<SQSEvent.SQSMessage> iter = input.getRecords().iterator();
        List<PostStatusDTO> myList;

        while (iter.hasNext()) {
            SQSEvent.SQSMessage sqsMessage = iter.next();
            Type listOfDTO = new TypeToken<ArrayList<PostStatusDTO>>() {}.getType();
            Gson gson = new Gson();
            myList = gson.fromJson(sqsMessage.getBody(), listOfDTO);
            statusService.postToFeeds(myList);
        }

        return null;
    }
}
