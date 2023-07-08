package se.edinjakupovic.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.edinjakupovic.api.model.Message;
import se.edinjakupovic.api.model.MessageResponse;
import se.edinjakupovic.request.Request;
import se.edinjakupovic.request.RequestHandler;
import se.edinjakupovic.request.Response;

public record MessageHandler() implements RequestHandler<Message, MessageResponse> {
    private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);

    @Override
    public Response<MessageResponse> handle(Request<Message> request) {
        log.info("Received request {}", request);
        return Response.ok(new MessageResponse("ok"));
    }

    @Override
    public Class<Message> requestType() {
        return Message.class;
    }
}
