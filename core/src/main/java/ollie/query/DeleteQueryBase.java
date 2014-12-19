package ollie.query;

import ollie.Model;
import ollie.util.QueryUtils;

import java.util.List;

abstract class DeleteQueryBase<T extends Model> extends ExecutableQueryBase<T> {
	DeleteQueryBase(Query parent, Class<T> table) {
		super(parent, table);
	}

	@Override
	public void execute() {
		execute(true);
	}

	/**
	 * Execute the delete statement and optionally update the entity id to null. Updating the id requires a select
	 * query of the rows to delete so if speed is a concern use execute(false) instead of execute().
	 * @param update Whether to update the entity id.
	 */
	public void execute(boolean update) {
		if (update) {
			final String sql = getSql().replace("DELETE", "SELECT " + Model._ID);
			final List<T> results = QueryUtils.rawQuery(mTable, sql, getArgs());
			for (T result : results) {
				result.id = null;
			}
		}
		super.execute();
	}
}
