package ollie.query;

import ollie.Model;

public abstract class QueryAdapter extends QueryBase {
	public QueryAdapter(Query parent, Class<? extends Model> table) {
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
