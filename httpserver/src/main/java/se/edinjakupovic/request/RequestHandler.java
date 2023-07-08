package se.edinjakupovic.request;

public interface RequestHandler<REQUEST, RESPONSE> {
    Response<RESPONSE> handle(Request<REQUEST> request);

    Class<REQUEST> requestType();

}
