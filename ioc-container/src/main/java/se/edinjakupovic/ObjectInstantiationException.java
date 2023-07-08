package se.edinjakupovic;

public class ObjectInstantiationException extends RuntimeException {

    public ObjectInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectInstantiationException(Throwable cause) {
        super(cause);
    }
}
