package ollie.query;

import ollie.Model;

public abstract class ExecutableQueryAdapter extends ExecutableQueryBase {
	public ExecutableQueryAdapter(Query parent, Class<? extends Model> table) {
		super(parent, table);
	}

	@Override
	protected String getPartSql() {
		return null;
	}

	@Override
	protected String[] getPartArgs() {
		return null;
	}
}
