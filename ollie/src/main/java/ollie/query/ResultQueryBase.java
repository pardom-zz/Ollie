package ollie.query;

import ollie.Model;
import ollie.Ollie;

import java.util.List;

public class ResultQueryBase extends ExecutableQueryBase implements ResultQuery {
	public ResultQueryBase(Query parent, Class<? extends Model> table) {
		super(parent, table);
	}

	@Override
	public <T extends Model> List<T> fetch() {
		return (List<T>) Ollie.rawQuery(mTable, getSql(), getArgs());
	}
}
