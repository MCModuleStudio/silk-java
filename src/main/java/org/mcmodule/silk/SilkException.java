package org.mcmodule.silk;

public class SilkException extends RuntimeException {

	private static final long serialVersionUID = -8647357082408744117L;

	public SilkException() {
		super();
	}

	public SilkException(String message) {
		super(message);
	}

	public SilkException(Throwable cause) {
		super(cause);
	}

	public static SilkException createFromErrorCodes(int errno) {
		SilkErrors errorType = SilkErrors.getErrorType(errno);
		return errorType == SilkErrors.UNKNOWN_ERROR ? new SilkException(String.format("Unknown Error: %d", errno)) : new SilkException(errorType.getReason());
	}
	
}
