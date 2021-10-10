package ys.cloud.sbot.exceptions;

public class ResourceNotFoundException extends RuntimeException {


    public ResourceNotFoundException( String message) {
        super(message);
    }
}