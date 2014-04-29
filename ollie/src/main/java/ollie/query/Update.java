package ollie.query;

import ollie.Model;
import ollie.Ollie;
import ollie.internal.TextUtils;
import ollie.query.util.ArrayUtils;

public class Update implements Query {
	private Class<? extends Model> mTable;

	public Update(Class<? extends Model> table) {
		mTable = table;
	}

	public Set set(String set) {
		return set(set, null);
	}

	public Set set(String set, String... args) {
		return new Set(this, mTable, set, args);
	}

	@Override
	public String getSql() {
		return "UPDATE " + Ollie.getTableName(mTable) + " ";
	}

	@Override
	public String[] getArgs() {
		return null;
	}

	public static final class Set implements ExecutableQuery {
		private Query mParent;
		private Class<? extends Model> mTable;
		private String mSet;
		private String[] mSetArgs;
		private String mWhere;
		private String[] mWhereArgs;

		private Set(Query parent, Class<? extends Model> table, String set, String... args) {
			mParent = parent;
			mTable = table;
			mSet = set;
			mSetArgs = args;
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
			Ollie.rawQuery(mTable, getSql(), getArgs());
		}

		@Override
		public String getSql() {
			StringBuilder builder = new StringBuilder();
			builder.append(mParent.getSql());
			builder.append("SET ");
			builder.append(mSet);

			if (!TextUtils.isEmpty(mWhere)) {
				builder.append(" WHERE ").append(mWhere);
			}

			return builder.toString();
		}

		@Override
		public String[] getArgs() {
			return ArrayUtils.addAll(mSetArgs, mWhereArgs);
		}
	}

	public static final class Tail implements ExecutableQuery {
		private Set mParent;

		private Tail(Set parent) {
			mParent = parent;
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
