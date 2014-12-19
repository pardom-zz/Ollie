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

import ollie.Model;
import ollie.Ollie;

public final class Delete extends QueryBase {
	private Delete() {
		super(null, null);
	}

	public static <T extends Model> From from(Class<T> table) {
		return new From<T>(new Delete(), table);
	}

	@Override
	public String getPartSql() {
		return "DELETE";
	}

	public static final class From<T extends Model> extends ExecutableQueryBase<T> {
		private From(Query parent, Class<T> table) {
			super(parent, table);
		}

		public Where<T> where(String where) {
			return where(where, (Object[]) null);
		}

		public Where<T> where(String where, Object... args) {
			return new Where<T>(this, mTable, where, args);
		}

		@Override
		public String getPartSql() {
			return "FROM " + Ollie.getTableName(mTable);
		}
	}

	public static final class Where<T extends Model> extends ExecutableQueryBase<T> {
		private String mWhere;
		private Object[] mWhereArgs;

		public Where(Query parent, Class<T> table, String where, Object[] args) {
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
			return toStringArray(mWhereArgs);
		}
	}
}
