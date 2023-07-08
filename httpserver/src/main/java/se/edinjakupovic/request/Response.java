package se.edinjakupovic.request;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static se.edinjakupovic.request.Constants.OK;

public record Response<T>(int statusCode, T value, Map<String, List<String>> headers) {
    public static final Response<Void> EMPTY_RESPONSE = new Response<>(200, null, emptyMap());

    public static <T> Response<T> ok(T value) {
        return new Response<>(OK, value, emptyMap());
    }
}
