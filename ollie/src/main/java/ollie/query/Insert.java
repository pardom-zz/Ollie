package ollie.query;

import ollie.Model;
import ollie.Ollie;
import ollie.internal.TextUtils;

public class Insert implements Query {
	public Into into(Class<? extends Model> table) {
		return new Into(this, table);
	}

	public Into into(Class<? extends Model> table, String... columns) {
		return new Into(this, table, columns);
	}

	@Override
	public String getSql() {
		return "INSERT ";
	}

	@Override
	public String[] getArgs() {
		return null;
	}

	public static final class Into implements Query {
		private Query mParent;
		private Class<? extends Model> mTable;
		private String[] mColumns;
		private String[] mValuesArgs;

		private Into(Query parent, Class<? extends Model> table, String... columns) {
			mParent = parent;
			mTable = table;
			mColumns = columns;
		}

		public Tail values(String... args) {
			mValuesArgs = args;
			return new Tail(this);
		}

		@Override
		public String getSql() {
			StringBuilder builder = new StringBuilder();
			builder.append(mParent.getSql());
			builder.append("INTO ");
			builder.append(Ollie.getTableName(mTable));
			if (mColumns != null && mColumns.length > 0) {
				builder.append("(").append(TextUtils.join(", ", mColumns)).append(")");
			}

			builder.append(" VALUES(");
			for (int i = 0; i < mValuesArgs.length; i++) {
				if (i > 0) {
					builder.append(", ");
				}
				builder.append("?");
			}
			builder.append(")");

			return builder.toString();
		}

		@Override
		public String[] getArgs() {
			return mValuesArgs;
		}
	}

	public static final class Tail implements ExecutableQuery {
		private Into mParent;

		private Tail(Into parent) {
			mParent = parent;
		}

		@Override
		public void execute() {
			if (mParent.mColumns != null && mParent.mColumns.length != mParent.mValuesArgs.length) {
				throw new MalformedQueryException("Number of columns does not match number of values.");
			}

			Ollie.rawQuery(mParent.mTable, getSql(), getArgs());
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
