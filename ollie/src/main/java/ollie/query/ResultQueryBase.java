package ollie.query;

import ollie.Model;
import ollie.Ollie;

import java.util.List;

public abstract class ResultQueryBase extends ExecutableQueryBase implements ResultQuery {
	public ResultQueryBase(Query parent, Class<? extends Model> table) {
		super(parent, table);
	}

	@Override
	public <T extends Model> List<T> fetch() {
		return (List<T>) Ollie.rawQuery(mTable, getSql(), getArgs());
	}

	@Override
	public <T extends Model> T fetchSingle() {
		List<T> results = (List<T>) Ollie.rawQuery(mTable, getSql(), getArgs());
		if (!results.isEmpty()) {
			return results.get(0);
		}
		return null;
	}
}
