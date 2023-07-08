package se.edinjakupovic.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.edinjakupovic.request.Request;
import se.edinjakupovic.request.RequestHandler;
import se.edinjakupovic.request.Response;

import static se.edinjakupovic.request.Response.EMPTY_RESPONSE;

public class TextHandler implements RequestHandler<String, Void> {
    private static final Logger log = LoggerFactory.getLogger(TextHandler.class);

    @Override
    public Response<Void> handle(Request<String> request) {
        log.info("Got message {}", request.value());
        return EMPTY_RESPONSE;
    }

    @Override
    public Class<String> requestType() {
        return String.class;
    }
}
