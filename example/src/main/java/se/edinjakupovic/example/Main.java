package se.edinjakupovic.example;


import se.edinjakupovic.ApplicationContext;
import se.edinjakupovic.Configuration;
import se.edinjakupovic.HTTPServer;
import se.edinjakupovic.Provides;
import se.edinjakupovic.request.Request;
import se.edinjakupovic.request.RequestFactory;
import se.edinjakupovic.request.RequestHandler;
import se.edinjakupovic.request.Response;
import se.edinjakupovic.routing.Router;

import static se.edinjakupovic.routing.UrlPartsParser.parse;

@Configuration
public class Main {


    record User(String name, int age) {
    }

    @Provides
    HTTPServer server() {
        Router router = new Router(new RequestFactory());
        router.registerRoute(parse("/user/:lastname"), new RequestHandler<String, User>() {

            @Override
            public Response<User> handle(Request<String> request) {
                String lastname = request.pathVariables().get(0);
                return Response.ok(new User("John " + lastname, 123));
            }

            @Override
            public Class<String> requestType() {
                return String.class;
            }
        });
        return new HTTPServer(router);
    }

    public static void main(String[] args) {
        ApplicationContext.fromConfiguration(Main.class)
                .getInstance(HTTPServer.class)
                .start();
    }
}