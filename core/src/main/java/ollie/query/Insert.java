package ollie.query;

import android.text.TextUtils;
import ollie.Model;
import ollie.Ollie;

public final class Insert extends QueryBase {
	public Insert() {
		super(null, null);
	}

	public Into into(Class<? extends Model> table) {
		return new Into(this, table);
	}

	public Into into(Class<? extends Model> table, String... columns) {
		return new Into(this, table, columns);
	}

	@Override
	public String getPartSql() {
		return "INSERT";
	}

	public static final class Into extends QueryBase {
		private String[] mColumns;

		private Into(Query parent, Class<? extends Model> table, String... columns) {
			super(parent, table);
			mColumns = columns;
		}

		public Values values(String... args) {
			return new Values(this, mTable, args);
		}

		@Override
		protected String getPartSql() {
			StringBuilder builder = new StringBuilder();
			builder.append("INTO ");
			builder.append(Ollie.getTableName(mTable));
			if (mColumns != null && mColumns.length > 0) {
				builder.append("(").append(TextUtils.join(", ", mColumns)).append(")");
			}

			return builder.toString();
		}
	}

	public static final class Values extends ExecutableQueryBase {
		private String[] mValuesArgs;

		private Values(Query parent, Class<? extends Model> table, String[] args) {
			super(parent, table);
			mValuesArgs = args;
		}

		@Override
		public void execute() {
			if (((Into) mParent).mColumns != null && ((Into) mParent).mColumns.length != mValuesArgs.length) {
				throw new MalformedQueryException("Number of columns does not match number of values.");
			}
			super.execute();
		}

		@Override
		protected String getPartSql() {
			StringBuilder builder = new StringBuilder();
			builder.append("VALUES(");
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
		protected String[] getPartArgs() {
			return mValuesArgs;
		}
	}
}
