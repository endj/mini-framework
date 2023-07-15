package se.edinjakupovic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.edinjakupovic.request.Response;
import se.edinjakupovic.request.RoutingException;
import se.edinjakupovic.routing.Router;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static se.edinjakupovic.request.Constants.INTERNAL_SERVER_ERROR;
import static se.edinjakupovic.request.Constants.NOT_FOUND;
import static se.edinjakupovic.request.Constants.NO_RESPONSE_BODY;

public class HTTPServer {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(HTTPServer.class);
    private HttpServer serverReference;
    private static final int BACKLOG;
    private static final int THREAD_POOL_SIZE;
    private static final int PORT;
    private static final boolean LOG_REQUESTS;

    static {
        System.setProperty("sun.net.httpserver.nodelay", "true");
        BACKLOG = intEnv("BACKLOG", 0);
        THREAD_POOL_SIZE = intEnv("THREAD_POOL_SIZE", 50);
        PORT = intEnv("PORT", 8080);
        LOG_REQUESTS = "true".equals(System.getenv("LOG_REQUEST"));
    }

    private static int intEnv(String key, int orElse) {
        var v = System.getenv(key);
        if (v == null)
            return orElse;
        return Integer.parseInt(v);
    }

    private final Router router;

    public HTTPServer(Router router) {
        this.router = router;
    }

    public void start() {

        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), BACKLOG);
            serverReference = httpServer;
            // TODO: 2023-07-08 Replace with virtual thread-pool executor
            httpServer.setExecutor(Executors.newFixedThreadPool(THREAD_POOL_SIZE));
            httpServer.createContext("/", exchange -> {
                if (LOG_REQUESTS) log.info("Received request " + exchange.getRequestURI());
                try {
                    Response<?> response = handleRequest(exchange);
                    writeResponse(exchange, response);
                } catch (RoutingException re) {
                    log.warn("Unable to route " + exchange.getRequestURI());
                    exchange.sendResponseHeaders(NOT_FOUND, NO_RESPONSE_BODY);
                } catch (Exception e) {
                    log.error("Internal Server Error ", e);
                    exchange.sendResponseHeaders(INTERNAL_SERVER_ERROR, NO_RESPONSE_BODY);
                } finally {
                    exchange.close();
                }
            });
            httpServer.start();
            log.info("Started server on localhost:8080");
        } catch (Exception e) {
            log.error("ERROR!", e);
            throw new RuntimeException(e);
        }
    }

    private void writeResponse(HttpExchange exchange, Response<?> response) {
        try {
            if (!response.headers().isEmpty()) {
                Headers responseHeaders = exchange.getResponseHeaders();
                response.headers().forEach((key, values) -> values.forEach(val -> responseHeaders.add(key, val)));
            }
            if (response.value() != null) {
                String responseString;
                if (response.value() instanceof String val) {
                    responseString = val;
                } else {
                    ObjectWriter objectWriter = MAPPER.writerFor(response.value().getClass());
                    responseString = objectWriter.writeValueAsString(response.value());
                }
                exchange.sendResponseHeaders(response.statusCode(), responseString.length());
                exchange.getResponseBody().write(responseString.getBytes(UTF_8));
            } else {
                exchange.sendResponseHeaders(response.statusCode(), NO_RESPONSE_BODY);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Response<?> handleRequest(HttpExchange exchange) {
        String[] parts = exchange.getRequestURI().toString().split("/");
        return router.handleRequest(parts, exchange);
    }

    public void stop() {
        log.info("Shutting down");
        serverReference.stop(5);
    }
}
