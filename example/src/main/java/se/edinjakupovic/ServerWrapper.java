package se.edinjakupovic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.edinjakupovic.lifecycle.OnInit;
import se.edinjakupovic.lifecycle.OnShutdown;

public class ServerWrapper {
    private static final Logger log = LoggerFactory.getLogger(ServerWrapper.class);

    HTTPServer server;

    public ServerWrapper(HTTPServer server) {
        this.server = server;
    }

    @OnInit
    public void init() {
        log.info("Starting up the server");
        server.start();
    }

    @OnShutdown
    public void stop() {
        log.info("Stopping server");
        server.stop();
    }
}
