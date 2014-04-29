package ollie.query;

import ollie.Model;
import ollie.query.util.ArrayUtils;

public abstract class QueryBase implements Query {
	protected Query mParent;
	protected Class<? extends Model> mTable;

	protected abstract String getPartSql();

	protected abstract String[] getPartArgs();

	public QueryBase(Query parent, Class<? extends Model> table) {
		mParent = parent;
		mTable = table;
	}

	@Override
	public final String getSql() {
		if (mParent != null) {
			return mParent.getSql() + " " + getPartSql().trim();
		}
		return getPartSql().trim();
	}

	@Override
	public final String[] getArgs() {
		if (mParent != null) {
			return ArrayUtils.addAll(mParent.getArgs(), getPartArgs());
		}
		return getPartArgs();
	}
}
