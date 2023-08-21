package se.edinjakupovic.request;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface RequestHandler<REQUEST, RESPONSE> {
    Response<RESPONSE> handle(Request<REQUEST> request);

    @SuppressWarnings("unchecked")
    default Class<REQUEST> requestType() {
        var c = this.getClass();
        for (Type genericInterface : c.getGenericInterfaces()) {
            if (genericInterface instanceof ParameterizedType t) {
                Type[] actualTypeArguments = t.getActualTypeArguments();
                return (Class<REQUEST>) actualTypeArguments[0];
            }
        }
        throw new IllegalStateException("""
                Failed to resolve request type.
                No support for lambdas atm unless you override resolveType :(
                """);
    }
}
