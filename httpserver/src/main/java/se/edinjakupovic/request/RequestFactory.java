package se.edinjakupovic.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestFactory {


    private static final ObjectMapper MAPPER = new ObjectMapper();

    public <T> Request<T> from(HttpExchange exchange, Class<T> type) {
        return from(exchange, null, null, type);
    }

    public <T> Request<T> from(HttpExchange exchange, List<String> pathVariables, Map<String, String> requestParameters, Class<T> type) {
        if ("POST".equals(exchange.getRequestMethod())) {
            try {
                byte[] bytes = exchange.getRequestBody().readAllBytes();
                if (type == String.class) {
                    return stringResponse(pathVariables, requestParameters, type, bytes);
                }

                ObjectReader reader = MAPPER.readerFor(type);

                return new Request<>(
                        pathVariables,
                        requestParameters,
                        reader.readValue(bytes),
                        type);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new Request<>(pathVariables, requestParameters, null, type);
    }

    @SuppressWarnings("unchecked")
    private static <T> Request<T> stringResponse(List<String> pathVariables, Map<String, String> requestParameters, Class<T> type, byte[] bytes) {
        String s = new String(bytes);
        return new Request<>(
                pathVariables,
                requestParameters,
                (T) s,
                type
        );
    }


    public Map<String, String> parseRequestParams(String parameters) {
        if (parameters == null || parameters.isBlank())
            throw new IllegalArgumentException();

        var params = new HashMap<String, String>();
        var kvPairs = parameters.split("&");
        for (String kvPair : kvPairs) {
            var parts = kvPair.split("=");
            if (parts.length != 2)
                throw new IllegalArgumentException("Malformed request parameters" + parameters);
            params.put(parts[0], parts[1]);
        }
        return params;
    }
}
