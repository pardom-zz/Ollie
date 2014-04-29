package ollie.query;

import ollie.Model;
import ollie.Ollie;
import ollie.internal.TextUtils;

public final class Delete implements Query {
	public From from(Class<? extends Model> table) {
		return new From(this, table);
	}

	@Override
	public String getSql() {
		return "DELETE ";
	}

	@Override
	public String[] getArgs() {
		return null;
	}

	public static final class From implements ExecutableQuery {
		private Query mParent;
		private Class<? extends Model> mTable;
		private String mWhere;
		private String[] mWhereArgs;

		private From(Query parent, Class<? extends Model> table) {
			mParent = parent;
			mTable = table;
		}

		public Tail where(String where) {
			return where(where, null);
		}

		public Tail where(String where, String... args) {
			mWhere = where;
			mWhereArgs = args;
			return new Tail(this);
		}

		@Override
		public void execute() {
		}

		@Override
		public String getSql() {
			StringBuilder builder = new StringBuilder();
			builder.append(mParent.getSql());
			builder.append("FROM ");
			builder.append(Ollie.getTableName(mTable));

			if (!TextUtils.isEmpty(mWhere)) {
				builder.append(" WHERE ").append(mWhere);
			}

			return builder.toString();
		}

		@Override
		public String[] getArgs() {
			return mWhereArgs;
		}
	}

	public static final class Tail implements ExecutableQuery {
		private From mParent;

		private Tail(From from) {
			mParent = from;
		}

		@Override
		public void execute() {
			mParent.execute();
		}

		@Override
		public String getSql() {
			return mParent.getSql();
		}

		@Override
		public String[] getArgs() {
			return mParent.getArgs();
		}
	}
}
