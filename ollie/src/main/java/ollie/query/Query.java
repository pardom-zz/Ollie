package ollie.query;

public interface Query {
	String getSql();

	String[] getArgs();
}