package se.edinjakupovic.largerexample;

import se.edinjakupovic.Configuration;
import se.edinjakupovic.HTTPServer;
import se.edinjakupovic.Provides;
import se.edinjakupovic.largerexample.api.MessageHandler;
import se.edinjakupovic.largerexample.api.TextHandler;
import se.edinjakupovic.largerexample.api.model.User;
import se.edinjakupovic.request.Request;
import se.edinjakupovic.request.RequestFactory;
import se.edinjakupovic.request.RequestHandler;
import se.edinjakupovic.request.Response;
import se.edinjakupovic.routing.Router;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@SuppressWarnings("unused")
@Configuration
public class Config {

    @Provides
    Router router(RequestFactory requestFactory) {
        return new Router(requestFactory)
                .registerRoute("/message", new MessageHandler())
                .registerRoute("/hello/world", new TextHandler())
                .registerRoute("/user/:lastname", new RequestHandler<String, User>() {

                    @Override
                    public Response<User> handle(Request<String> request) {
                        String lastname = request.pathVariables().get(0);
                        return Response.ok(new User("John " + lastname, 123));
                    }
                });
    }

    @Provides
    RequestFactory requestFactory() {
        return new RequestFactory();
    }

    @Provides
    HTTPServer server(Router router) {
        return new HTTPServer(router);
    }

    @Provides
    ServerWrapper serverWrapper(HTTPServer server) {
        return new ServerWrapper(server);
    }

    @Provides
    ServerPinger pinger() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        return new ServerPinger(scheduledExecutorService);
    }

}
