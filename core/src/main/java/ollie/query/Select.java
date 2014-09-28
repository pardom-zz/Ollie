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

public final class Select extends QueryBase {
	private String[] mColumns;

	public Select() {
		super(null, null);
	}

	public Select(String... columns) {
		super(null, null);
		mColumns = columns;
	}

	public From from(Class<? extends Model> table) {
		return new From(this, table);
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

	public static final class From extends ResultQueryBase {
		private List<Join> mJoins = new ArrayList<Join>();

		public From(Query parent, Class<? extends Model> table) {
			super(parent, table);
		}

		public Join join(Class<? extends Model> table) {
			return addJoin(table, Join.Type.JOIN);
		}

		public Join leftJoin(Class<? extends Model> table) {
			return addJoin(table, Join.Type.LEFT);
		}

		public Join leftOuterJoin(Class<? extends Model> table) {
			return addJoin(table, Join.Type.LEFT_OUTER);
		}

		public Join innerJoin(Class<? extends Model> table) {
			return addJoin(table, Join.Type.INNER);
		}

		public Join crossJoin(Class<? extends Model> table) {
			return addJoin(table, Join.Type.CROSS);
		}

		public Join naturalJoin(Class<? extends Model> table) {
			return addJoin(table, Join.Type.NATURAL_JOIN);
		}

		public Join naturalLeftJoin(Class<? extends Model> table) {
			return addJoin(table, Join.Type.NATURAL_LEFT);
		}

		public Join naturalLeftOuterJoin(Class<? extends Model> table) {
			return addJoin(table, Join.Type.NATURAL_LEFT_OUTER);
		}

		public Join naturalInnerJoin(Class<? extends Model> table) {
			return addJoin(table, Join.Type.NATURAL_INNER);
		}

		public Join naturalCrossJoin(Class<? extends Model> table) {
			return addJoin(table, Join.Type.NATURAL_CROSS);
		}

		public Where where(String where) {
			return new Where(this, mTable, where, null);
		}

		public Where where(String where, String... args) {
			return new Where(this, mTable, where, args);
		}

		public GroupBy groupBy(String groupBy) {
			return new GroupBy(this, mTable, groupBy);
		}

		public OrderBy orderBy(String orderBy) {
			return new OrderBy(this, mTable, orderBy);
		}

		public Limit limit(String limit) {
			return new Limit(this, mTable, limit);
		}

		private Join addJoin(Class<? extends Model> table, Join.Type type) {
			final Join join = new Join(this, table, type);
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

	public static final class Join extends QueryBase {
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

		public Join(Query parent, Class<? extends Model> table, Type type) {
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

	public static final class Where extends ResultQueryBase {
		private String mWhere;
		private String[] mWhereArgs;

		public Where(Query parent, Class<? extends Model> table, String where, String[] args) {
			super(parent, table);
			mWhere = where;
			mWhereArgs = args;
		}

		public GroupBy groupBy() {
			return null;
		}

		public OrderBy orderBy(String orderBy) {
			return new OrderBy(this, mTable, orderBy);
		}

		public Limit limit(String limits) {
			return new Limit(this, mTable, limits);
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

	public static final class GroupBy extends ResultQueryBase {
		private String mGroupBy;

		public GroupBy(Query parent, Class<? extends Model> table, String groupBy) {
			super(parent, table);
			mGroupBy = groupBy;
		}

		public Having having(String having) {
			return new Having(this, mTable, having);
		}

		public OrderBy orderBy(String orderBy) {
			return new OrderBy(this, mTable, orderBy);
		}

		public Limit limit(String limits) {
			return new Limit(this, mTable, limits);
		}

		@Override
		public String getPartSql() {
			return "GROUP BY " + mGroupBy;
		}
	}

	public static final class Having extends ResultQueryBase {
		private String mHaving;

		public Having(Query parent, Class<? extends Model> table, String having) {
			super(parent, table);
			mHaving = having;
		}

		public OrderBy orderBy(String orderBy) {
			return new OrderBy(this, mTable, orderBy);
		}

		public Limit limit(String limits) {
			return new Limit(this, mTable, limits);
		}

		@Override
		public String getPartSql() {
			return "HAVING " + mHaving;
		}
	}

	public static final class OrderBy extends ResultQueryBase {
		private String mOrderBy;

		public OrderBy(Query parent, Class<? extends Model> table, String orderBy) {
			super(parent, table);
			mOrderBy = orderBy;
		}

		public Limit limit(String limits) {
			return new Limit(this, mTable, limits);
		}

		@Override
		public String getPartSql() {
			return "ORDER BY " + mOrderBy;
		}

	}

	public static final class Limit extends ResultQueryBase {
		private String mLimit;

		public Limit(Query parent, Class<? extends Model> table, String limit) {
			super(parent, table);
			mLimit = limit;
		}

		public Offset offset(String offset) {
			return new Offset(this, mTable, offset);
		}

		@Override
		public String getPartSql() {
			return "LIMIT " + mLimit;
		}
	}

	public static final class Offset extends ResultQueryBase {
		private String mOffset;

		public Offset(Query parent, Class<? extends Model> table, String offset) {
			super(parent, table);
			mOffset = offset;
		}

		@Override
		protected String getPartSql() {
			return "OFFSET " + mOffset;
		}
	}
}
