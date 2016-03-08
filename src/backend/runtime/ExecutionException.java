package backend.runtime;

public class ExecutionException extends RuntimeException {
	public ExecutionException(String message) {
		super(message);
	}

	public ExecutionException(String message, Throwable cause) {
		super(message, cause);
		cause.printStackTrace();
	}
}
