package se.edinjakupovic.testconfigs;

public class LifeCycleException extends RuntimeException {
    public LifeCycleException(String message) {
        super(message);
    }

    public LifeCycleException() {
    }
}
