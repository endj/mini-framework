package se.edinjakupovic.request;

import java.util.List;
import java.util.Map;

public record Request<T>(
        List<String> pathVariables,
        Map<String, String> requestParameters,
        T value,
        Class<T> type) {
}
