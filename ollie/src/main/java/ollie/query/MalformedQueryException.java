package ollie.query;

public final class MalformedQueryException extends RuntimeException {
	public MalformedQueryException(String detailMessage) {
		super(detailMessage);
	}
}