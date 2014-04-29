package ollie.query;

public interface Query {
	String getSql();

	String[] getArgs();

	public static final class MalformedQueryException extends RuntimeException {
		public MalformedQueryException(String detailMessage) {
			super(detailMessage);
		}
	}
}