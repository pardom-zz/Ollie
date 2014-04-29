package ollie.query;

import ollie.Model;

import java.util.List;

public interface ResultQuery extends ExecutableQuery {
	<T extends Model> List<T> fetch();

	<T extends Model> T fetchSingle();
}
