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

import java.util.ArrayList;
import java.util.List;

public final class Select<T extends Model> extends QueryBase<T> {
	private String[] mColumns;

	private Select() {
		super(null, null);
	}

	private Select(String... columns) {
		super(null, null);
		mColumns = columns;
	}

	public static <T extends Model> Columns<T> columns(String... columns) {
		return new Columns<T>(columns);
	}

	public static <T extends Model> From<T> from(Class<T> table) {
		return new From<T>(new Select<T>(), table);
	}

	@Override
	public String getPartSql() {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT ");
		if (mColumns != null && mColumns.length > 0) {
			builder.append(TextUtils.join(", ", mColumns)).append(" ");
		} else {
			builder.append("* ");
		}

		return builder.toString();
	}

	public static final class Columns<T extends Model> extends QueryBase<T> {
		private String[] mColumns;

		public Columns(String[] columns) {
			super(null, null);
			mColumns = columns;
		}

		public <T extends Model> From<T> from(Class<T> table) {
			return new From<T>(new Select<T>(mColumns), table);
		}
	}

	public static final class From<T extends Model> extends ResultQueryBase<T> {
		private List<Join> mJoins = new ArrayList<Join>();

		private From(Query parent, Class<T> table) {
			super(parent, table);
		}

		public <E extends Model> Join<E> join(Class<E> table) {
			return addJoin(table, Join.Type.JOIN);
		}

		public <E extends Model> Join<E> leftJoin(Class<E> table) {
			return addJoin(table, Join.Type.LEFT);
		}

		public <E extends Model> Join<E> leftOuterJoin(Class<E> table) {
			return addJoin(table, Join.Type.LEFT_OUTER);
		}

		public <E extends Model> Join<E> innerJoin(Class<E> table) {
			return addJoin(table, Join.Type.INNER);
		}

		public <E extends Model> Join<E> crossJoin(Class<E> table) {
			return addJoin(table, Join.Type.CROSS);
		}

		public <E extends Model> Join<E> naturalJoin(Class<E> table) {
			return addJoin(table, Join.Type.NATURAL_JOIN);
		}

		public <E extends Model> Join<E> naturalLeftJoin(Class<E> table) {
			return addJoin(table, Join.Type.NATURAL_LEFT);
		}

		public <E extends Model> Join<E> naturalLeftOuterJoin(Class<E> table) {
			return addJoin(table, Join.Type.NATURAL_LEFT_OUTER);
		}

		public <E extends Model> Join<E> naturalInnerJoin(Class<E> table) {
			return addJoin(table, Join.Type.NATURAL_INNER);
		}

		public <E extends Model> Join<E> naturalCrossJoin(Class<E> table) {
			return addJoin(table, Join.Type.NATURAL_CROSS);
		}

		public Where<T> where(String where) {
			return new Where<T>(this, mTable, where, null);
		}

		public Where<T> where(String where, Object... args) {
			return new Where<T>(this, mTable, where, args);
		}

		public GroupBy<T> groupBy(String groupBy) {
			return new GroupBy<T>(this, mTable, groupBy);
		}

		public OrderBy<T> orderBy(String orderBy) {
			return new OrderBy<T>(this, mTable, orderBy);
		}

		public Limit<T> limit(String limit) {
			return new Limit<T>(this, mTable, limit);
		}

		private <E extends Model> Join<E> addJoin(Class<E> table, Join.Type type) {
			final Join<E> join = new Join<E>(this, table, type);
			mJoins.add(join);
			return join;
		}

		@Override
		public String getPartSql() {
			StringBuilder builder = new StringBuilder();
			builder.append("FROM ");
			builder.append(Ollie.getTableName(mTable)).append(" ");

			for (Join join : mJoins) {
				builder.append(join.getPartSql()).append(" ");
			}

			return builder.toString();
		}
	}

	public static final class Join<T extends Model> extends QueryBase<T> {
		public enum Type {
			JOIN("JOIN"),
			LEFT("LEFT JOIN"),
			LEFT_OUTER("LEFT OUTER JOIN"),
			INNER("INNER JOIN"),
			CROSS("CROSS JOIN"),
			NATURAL_JOIN("NATURAL JOIN"),
			NATURAL_LEFT("NATURAL LEFT JOIN"),
			NATURAL_LEFT_OUTER("NATURAL LEFT OUTER JOIN"),
			NATURAL_INNER("NATURAL INNER JOIN"),
			NATURAL_CROSS("NATURAL CROSS JOIN");

			private String mKeyword;

			Type(String keyword) {
				mKeyword = keyword;
			}

			public String getKeyword() {
				return mKeyword;
			}
		}

		private Type mType;
		private String mConstraint;

		private Join(Query parent, Class<T> table, Type type) {
			super(parent, table);
			mType = type;
		}

		public From on(String constraint) {
			mConstraint = "ON " + constraint;
			return (From) mParent;
		}

		public From using(String... columns) {
			mConstraint = "USING (" + TextUtils.join(", ", columns) + ")";
			return (From) mParent;
		}

		@Override
		public String getPartSql() {
			return mType.getKeyword() + " " + Ollie.getTableName(mTable) + " " + mConstraint;
		}
	}

	public static final class Where<T extends Model> extends ResultQueryBase<T> {
		private String mWhere;
		private Object[] mWhereArgs;

		private Where(Query parent, Class<T> table, String where, Object[] args) {
			super(parent, table);
			mWhere = where;
			mWhereArgs = args;
		}

		public GroupBy<T> groupBy() {
			return null;
		}

		public OrderBy<T> orderBy(String orderBy) {
			return new OrderBy<T>(this, mTable, orderBy);
		}

		public Limit<T> limit(String limits) {
			return new Limit<T>(this, mTable, limits);
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

	public static final class GroupBy<T extends Model> extends ResultQueryBase<T> {
		private String mGroupBy;

		private GroupBy(Query parent, Class<T> table, String groupBy) {
			super(parent, table);
			mGroupBy = groupBy;
		}

		public Having<T> having(String having) {
			return new Having<T>(this, mTable, having);
		}

		public OrderBy<T> orderBy(String orderBy) {
			return new OrderBy<T>(this, mTable, orderBy);
		}

		public Limit<T> limit(String limits) {
			return new Limit<T>(this, mTable, limits);
		}

		@Override
		public String getPartSql() {
			return "GROUP BY " + mGroupBy;
		}
	}

	public static final class Having<T extends Model> extends ResultQueryBase<T> {
		private String mHaving;

		private Having(Query parent, Class<T> table, String having) {
			super(parent, table);
			mHaving = having;
		}

		public OrderBy<T> orderBy(String orderBy) {
			return new OrderBy<T>(this, mTable, orderBy);
		}

		public Limit<T> limit(String limits) {
			return new Limit<T>(this, mTable, limits);
		}

		@Override
		public String getPartSql() {
			return "HAVING " + mHaving;
		}
	}

	public static final class OrderBy<T extends Model> extends ResultQueryBase<T> {
		private String mOrderBy;

		private OrderBy(Query parent, Class<T> table, String orderBy) {
			super(parent, table);
			mOrderBy = orderBy;
		}

		public Limit<T> limit(String limits) {
			return new Limit<T>(this, mTable, limits);
		}

		@Override
		public String getPartSql() {
			return "ORDER BY " + mOrderBy;
		}

	}

	public static final class Limit<T extends Model> extends ResultQueryBase<T> {
		private String mLimit;

		private Limit(Query parent, Class<T> table, String limit) {
			super(parent, table);
			mLimit = limit;
		}

		public Offset<T> offset(String offset) {
			return new Offset<T>(this, mTable, offset);
		}

		@Override
		public String getPartSql() {
			return "LIMIT " + mLimit;
		}
	}

	public static final class Offset<T extends Model> extends ResultQueryBase<T> {
		private String mOffset;

		private Offset(Query parent, Class<T> table, String offset) {
			super(parent, table);
			mOffset = offset;
		}

		@Override
		protected String getPartSql() {
			return "OFFSET " + mOffset;
		}
	}
}
