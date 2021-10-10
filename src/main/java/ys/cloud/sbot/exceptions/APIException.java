package ys.cloud.sbot.exceptions;

public class APIException extends RuntimeException {
	
	
    public APIException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public APIException(Throwable arg0) {
		super(arg0);
	}

	public APIException(String message) {
        super(message);
    }
}