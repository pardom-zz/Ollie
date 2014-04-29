package ollie.query;

import ollie.Model;
import ollie.Ollie;

public class Update extends QueryAdapter {
	public Update(Class<? extends Model> table) {
		super(null, table);
	}

	public Set set(String set) {
		return set(set, null);
	}

	public Set set(String set, String... args) {
		return new Set(this, mTable, set, args);
	}

	@Override
	public String getPartSql() {
		return "UPDATE " + Ollie.getTableName(mTable);
	}

	public static final class Set extends ExecutableQueryAdapter {
		private String mSet;
		private String[] mSetArgs;

		private Set(Query parent, Class<? extends Model> table, String set, String... args) {
			super(parent, table);
			mSet = set;
			mSetArgs = args;
		}

		public Where where(String where) {
			return where(where, null);
		}

		public Where where(String where, String... args) {
			return new Where(this, mTable, where, args);
		}

		@Override
		public String getPartSql() {
			return "SET " + mSet;
		}

		@Override
		public String[] getPartArgs() {
			return mSetArgs;
		}
	}

	public static final class Where extends ExecutableQueryAdapter {
		private String mWhere;
		private String[] mWhereArgs;

		public Where(Query parent, Class<? extends Model> table, String where, String[] args) {
			super(parent, table);
			mWhere = where;
			mWhereArgs = args;
		}

		@Override
		public String getPartSql() {
			return "WHERE " + mWhere;
		}

		@Override
		public String[] getPartArgs() {
			return mWhereArgs;
		}
	}
}
