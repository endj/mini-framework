package se.edinjakupovic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.edinjakupovic.lifecycle.OnInit;
import se.edinjakupovic.lifecycle.OnShutdown;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerPinger {
    private static final Logger log = LoggerFactory.getLogger(ServerPinger.class);

    private final ScheduledExecutorService scheduledExecutorService;
    private final HttpClient httpClient;

    public ServerPinger(ScheduledExecutorService scheduledExecutorService) {
        this.scheduledExecutorService = scheduledExecutorService;
        httpClient = HttpClient.newBuilder().build();
    }

    @OnInit
    void init() {
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                httpClient.send(
                        HttpRequest.newBuilder()
                                .POST(HttpRequest.BodyPublishers.ofString("""
                                        {
                                          "text": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor atat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
                                        }"""))
                                .uri(URI.create("http://localhost:8080/message"))
                                .build()
                        , responseInfo -> {
                            log.info("Got response {}", responseInfo.statusCode());
                            return HttpResponse.BodySubscribers.ofString(StandardCharsets.UTF_8);
                        });
            } catch (Exception e) {
                log.error("Woopsie", e);
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    @OnShutdown
    void stop() {
        log.info("Shutting down");
        scheduledExecutorService.shutdownNow();
    }
}
