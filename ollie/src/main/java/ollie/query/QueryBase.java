package ollie.query;

import ollie.Model;

public abstract class QueryBase implements Query {
	protected Query mParent;
	protected Class<? extends Model> mTable;

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
			return join(mParent.getArgs(), getPartArgs());
		}
		return getPartArgs();
	}

	protected String getPartSql() {
		return null;
	}

	protected String[] getPartArgs() {
		return null;
	}

	private String[] join(final String[] array1, final String... array2) {
		if (array1 == null) {
			return clone(array2);
		} else if (array2 == null) {
			return clone(array1);
		}
		final String[] joinedArray = new String[array1.length + array2.length];
		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		return joinedArray;
	}

	private String[] clone(final String[] array) {
		if (array == null) {
			return null;
		}
		return array.clone();
	}
}
