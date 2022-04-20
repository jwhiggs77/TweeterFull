package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import java.util.ArrayList;
import java.util.List;
import edu.byu.cs.tweeter.DTO.PostStatusDTO;
import edu.byu.cs.tweeter.model.net.JsonSerializer;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.concrete.DynamoFollowDAO;
import edu.byu.cs.tweeter.util.Pair;

public class PostUpdateFeedMessages implements RequestHandler<SQSEvent, Void> {
    String queueUrl = "https://sqs.us-west-2.amazonaws.com/121950966543/updateFeedQueue";
    List<PostStatusDTO> bundle = new ArrayList<>();
    FollowDAO followDAO = new DynamoFollowDAO();

    @Override
    public Void handleRequest(SQSEvent input, Context context) {
        PostStatusRequest request = JsonSerializer.deserialize(input.getRecords().get(0).getBody(), PostStatusRequest.class);
        boolean hasMorePages;

        String lastAlias = null;
        do {
            Pair<List<String>, Boolean> followers = followDAO.getFollowers(new FollowersRequest(request.getAuthToken(), request.getStatus().getUser().getAlias(), 100, lastAlias));

            hasMorePages = followers.getSecond();

            for (String alias : followers.getFirst()) {
                PostStatusDTO postStatusDTO = new PostStatusDTO(request.getStatus(), alias);
                bundle.add(postStatusDTO);
            }

            // serialize bundle and send to UpdateFeedsQueue
            String bundleMsg = JsonSerializer.serialize(bundle);
            SendMessageRequest send_msg_request = new SendMessageRequest().withQueueUrl(queueUrl).withMessageBody(bundleMsg);
            AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
            SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);

            // Reset your bundle/batch
            bundle = new ArrayList<>();
            lastAlias = followers.getFirst().get(followers.getFirst().size() - 1);

        } while(hasMorePages);

        return null;
    }
}
