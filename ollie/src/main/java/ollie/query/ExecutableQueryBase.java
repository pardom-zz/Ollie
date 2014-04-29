package ollie.query;

import ollie.Model;
import ollie.Ollie;

public abstract class ExecutableQueryBase extends QueryBase implements ExecutableQuery {
	public ExecutableQueryBase(Query parent, Class<? extends Model> table) {
		super(parent, table);
	}

	@Override
	public void execute() {
		Ollie.rawQuery(mTable, getSql(), getArgs());
	}
}
