package ollie.query;

import ollie.Model;
import ollie.Ollie;

public final class Delete extends QueryBase {
	public Delete() {
		super(null, null);
	}

	public From from(Class<? extends Model> table) {
		return new From(this, table);
	}

	@Override
	public String getPartSql() {
		return "DELETE";
	}

	public static final class From extends ExecutableQueryBase {
		private From(Query parent, Class<? extends Model> table) {
			super(parent, table);
		}

		public Where where(String where) {
			return where(where, (String[]) null);
		}

		public Where where(String where, String... args) {
			return new Where(this, mTable, where, args);
		}

		@Override
		public String getPartSql() {
			return "FROM " + Ollie.getTableName(mTable);
		}
	}

	public static final class Where extends ExecutableQueryBase {
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
