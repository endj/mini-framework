package se.edinjakupovic.request;


public record TypedRequestHandler<T, R>(
        RequestHandler<T, R> requestHandler,
        Class<T> requestType
) {
}
