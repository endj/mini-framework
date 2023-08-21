package se.edinjakupovic.simple;

import se.edinjakupovic.HTTPServer;
import se.edinjakupovic.request.Request;
import se.edinjakupovic.request.RequestFactory;
import se.edinjakupovic.request.RequestHandler;
import se.edinjakupovic.request.Response;
import se.edinjakupovic.routing.Router;

import static se.edinjakupovic.routing.UrlPartsParser.parse;

public class TestEchoServer {
    public static void main(String[] args) {
        new HTTPServer(new Router(new RequestFactory())
                .registerRoute("/hello", new RequestHandler<String, String>() {
                    @Override
                    public Response<String> handle(Request<String> request) {
                        return Response.ok(request.value());
                    }
                }))
                .start();
    }
}
