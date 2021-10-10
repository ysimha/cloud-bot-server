package ys.cloud.sbot.exceptions;

public class BuyOrderException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BuyOrderException() {
	}

	public BuyOrderException(String message) {
		super(message);
	}

	public BuyOrderException(Throwable cause) {
		super(cause);
	}

	public BuyOrderException(String message, Throwable cause) {
		super(message, cause);
	}

	public BuyOrderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
