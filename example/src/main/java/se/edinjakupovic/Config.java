package se.edinjakupovic;

import se.edinjakupovic.api.MessageHandler;
import se.edinjakupovic.api.TextHandler;
import se.edinjakupovic.request.RequestFactory;
import se.edinjakupovic.routing.Router;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static se.edinjakupovic.routing.UrlPartsParser.parse;

@SuppressWarnings("unused")
@Configuration
public class Config {

    @Provides
    Router router(RequestFactory requestFactory) {
        var router = new Router(requestFactory);
        router.registerRoute(parse("/message"), new MessageHandler());
        router.registerRoute(parse("/hello/world"), new TextHandler());
        return router;
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
