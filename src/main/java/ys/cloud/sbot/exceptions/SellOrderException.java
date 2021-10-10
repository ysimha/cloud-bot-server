package ys.cloud.sbot.exceptions;

public class SellOrderException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public SellOrderException() {
	}

	public SellOrderException(String message) {
		super(message);
	}

	public SellOrderException(Throwable cause) {
		super(cause);
	}

	public SellOrderException(String message, Throwable cause) {
		super(message, cause);
	}

	public SellOrderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
