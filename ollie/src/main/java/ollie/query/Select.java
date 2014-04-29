package ollie.query;

import ollie.Model;
import ollie.Ollie;
import ollie.internal.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class Select implements Query {
	private String[] mColumns;

	public Select() {
	}

	public Select(String... columns) {
		mColumns = columns;
	}

	public From from(Class<? extends Model> table) {
		return new From(this, table);
	}

	@Override
	public String getSql() {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT ");
		if (mColumns != null && mColumns.length > 0) {
			builder.append(TextUtils.join(", ", mColumns)).append(" ");
		} else {
			builder.append("* ");
		}

		return builder.toString();
	}

	@Override
	public String[] getArgs() {
		return null;
	}

	public static final class From implements ExecutableQuery {
		private Query mParent;
		private Class<? extends Model> mTable;
		private List<Join> mJoins = new ArrayList<Join>();

		public From(Query parent, Class<? extends Model> table) {
			mParent = parent;
			mTable = table;
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
			return new Where(this, where, null);
		}

		public Where where(String where, String... args) {
			return new Where(this, where, args);
		}

		public GroupBy groupBy(String groupBy) {
			return new GroupBy(this, groupBy);
		}

		public OrderBy orderBy(String orderBy) {
			return new OrderBy(this, orderBy);
		}

		public Limit limit(String limit) {
			return new Limit(this, limit);
		}

		private Join addJoin(Class<? extends Model> table, Join.Type type) {
			final Join join = new Join(this, table, type);
			mJoins.add(join);
			return join;
		}

		@Override
		public void execute() {
		}

		@Override
		public String getSql() {
			StringBuilder builder = new StringBuilder();
			builder.append(mParent.getSql());
			builder.append("FROM ");
			builder.append(Ollie.getTableName(mTable)).append(" ");

			for (Join join : mJoins) {
				builder.append(join.getSql()).append(" ");
			}

			return builder.toString().trim();
		}

		@Override
		public String[] getArgs() {
			return null;
		}
	}

	public static final class Join implements Query {
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

		private From mParent;
		private Class<? extends Model> mTable;
		private Type mType;
		private String mConstraint;

		public Join(From parent, Class<? extends Model> table, Type type) {
			mParent = parent;
			mTable = table;
			mType = type;
		}

		public From on(String constraint) {
			mConstraint = "ON " + constraint;
			return mParent;
		}

		public From using(String... columns) {
			mConstraint = "USING (" + TextUtils.join(", ", columns) + ")";
			return mParent;
		}

		@Override
		public String getSql() {
			return mType.getKeyword() + " " + Ollie.getTableName(mTable) + " " + mConstraint;
		}

		@Override
		public String[] getArgs() {
			return null;
		}
	}

	public static final class Where implements ExecutableQuery {
		private Query mParent;
		private String mWhere;
		private String[] mWhereArgs;

		public Where(Query parent, String where, String[] args) {
			mParent = parent;
			mWhere = where;
			mWhereArgs = args;
		}

		public GroupBy groupBy() {
			return null;
		}

		public OrderBy orderBy(String orderBy) {
			return new OrderBy(this, orderBy);
		}

		public Limit limit(String limits) {
			return new Limit(this, limits);
		}

		@Override
		public void execute() {

		}

		@Override
		public String getSql() {
			StringBuilder builder = new StringBuilder();
			builder.append(mParent.getSql());
			builder.append(" WHERE ");
			builder.append(mWhere);
			return builder.toString().trim();
		}

		@Override
		public String[] getArgs() {
			return mWhereArgs;
		}
	}

	public static final class GroupBy implements ExecutableQuery {
		private Query mParent;
		private String mGroupBy;

		public GroupBy(Query parent, String groupBy) {
			mParent = parent;
			mGroupBy = groupBy;
		}

		public Having having(String having) {
			return new Having(this, having);
		}

		public OrderBy orderBy(String orderBy) {
			return new OrderBy(this, orderBy);
		}

		public Limit limit(String limits) {
			return new Limit(this, limits);
		}

		@Override
		public void execute() {

		}

		@Override
		public String getSql() {
			return mParent.getSql() + " GROUP BY " + mGroupBy.trim();
		}

		@Override
		public String[] getArgs() {
			return mParent.getArgs();
		}
	}

	public static final class Having implements ExecutableQuery {
		private Query mParent;
		private String mHaving;

		public Having(Query parent, String having) {
			mParent = parent;
			mHaving = having;
		}

		public OrderBy orderBy(String orderBy) {
			return new OrderBy(this, orderBy);
		}

		public Limit limit(String limits) {
			return new Limit(this, limits);
		}

		@Override
		public void execute() {

		}

		@Override
		public String getSql() {
			return mParent.getSql() + " HAVING " + mHaving.trim();
		}

		@Override
		public String[] getArgs() {
			return mParent.getArgs();
		}
	}

	public static final class OrderBy implements ExecutableQuery {
		private Query mParent;
		private String mOrderBy;

		public OrderBy(Query parent, String orderBy) {
			mParent = parent;
			mOrderBy = orderBy;
		}

		public Limit limit(String limits) {
			return new Limit(this, limits);
		}

		@Override
		public void execute() {
		}

		@Override
		public String getSql() {
			return mParent.getSql() + " ORDER BY " + mOrderBy.trim();
		}

		@Override
		public String[] getArgs() {
			return mParent.getArgs();
		}
	}

	public static final class Limit implements ExecutableQuery {
		private Query mParent;
		private String mLimit;
		private String mOffset;

		public Limit(Query parent, String limit) {
			mParent = parent;
			mLimit = limit;
		}

		public Tail offset(String offset) {
			mOffset = offset;
			return new Tail(this);
		}

		@Override
		public void execute() {

		}

		@Override
		public String getSql() {
			StringBuilder builder = new StringBuilder();
			builder.append(mParent.getSql());
			builder.append(" LIMIT ");
			builder.append(mLimit.trim());

			if (!TextUtils.isEmpty(mOffset)) {
				builder.append(" OFFSET ");
				builder.append(mOffset.trim());
			}

			return builder.toString();
		}

		@Override
		public String[] getArgs() {
			return mParent.getArgs();
		}
	}

	public static final class Tail implements ExecutableQuery {
		private Limit mParent;

		public Tail(Limit parent) {
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
