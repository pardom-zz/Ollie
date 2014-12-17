/*
 * Copyright (C) 2014 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ollie.query;

import android.text.TextUtils;
import ollie.Model;
import ollie.Ollie;

public final class Insert<T extends Model> extends QueryBase<T> {
	private Insert() {
		super(null, null);
	}

	public static <T extends Model> Into<T> into(Class<T> table) {
		return new Into<T>(new Insert<T>(), table);
	}

	@Override
	public String getPartSql() {
		return "INSERT";
	}

	public static final class Into<T extends Model> extends QueryBase<T> {
		private Into(Query parent, Class<T> table) {
			super(parent, table);
		}

		public Columns<T> columns(String... columns) {
			return new Columns<T>(this, mTable, columns);
		}

		public Values<T> values(Object... args) {
			return new Values<T>(this, mTable, args);
		}

		@Override
		protected String getPartSql() {
			StringBuilder builder = new StringBuilder();
			builder.append("INTO ");
			builder.append(Ollie.getTableName(mTable));
			return builder.toString();
		}
	}

	public static final class Columns<T extends Model> extends QueryBase<T> {
		private String[] mColumns;

		public Columns(Query parent, Class<T> table, String[] columns) {
			super(parent, table);
			mColumns = columns;
		}

		public Values<T> values(Object... args) {
			if (mColumns.length != args.length) {
				throw new MalformedQueryException("Number of columns does not match number of values.");
			}
			return new Values<T>(this, mTable, args);
		}

		@Override
		protected String getPartSql() {
			StringBuilder builder = new StringBuilder();
			if (mColumns != null && mColumns.length > 0) {
				builder.append("(").append(TextUtils.join(", ", mColumns)).append(")");
			}
			return builder.toString();
		}
	}

	public static final class Values<T extends Model> extends ExecutableQueryBase<T> {
		private Object[] mValuesArgs;

		private Values(Query parent, Class<T> table, Object[] args) {
			super(parent, table);
			mValuesArgs = args;
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
			return toStringArray(mValuesArgs);
		}
	}
}
