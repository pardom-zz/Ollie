package ollie.query;

import android.database.Cursor;
import ollie.Model;
import ollie.Ollie;

import java.util.List;

public abstract class ResultQueryBase extends ExecutableQueryBase implements ResultQuery {
	public ResultQueryBase(Query parent, Class<? extends Model> table) {
		super(parent, table);
	}

	@Override
	public <T extends Model> List<T> fetch() {
		return (List<T>) Ollie.rawQuery(mTable, getSql(), getArgs());
	}

	@Override
	public <T extends Model> T fetchSingle() {
		List<T> results = (List<T>) Ollie.rawQuery(mTable, getSql(), getArgs());
		if (!results.isEmpty()) {
			return results.get(0);
		}
		return null;
	}

	@Override
	public <T> T fetchValue() {
		final Cursor cursor = Ollie.getDatabase().rawQuery(getSql(), getArgs());
		if (!cursor.moveToFirst()) {
			return null;
		}

		int type = cursor.getType(0);
		switch (type) {
			case Cursor.FIELD_TYPE_BLOB: {
				return (T) cursor.getBlob(0);
			}
			case Cursor.FIELD_TYPE_FLOAT: {
				return (T) Float.valueOf(cursor.getFloat(0));
			}
			case Cursor.FIELD_TYPE_INTEGER: {
				return (T) Integer.valueOf(cursor.getInt(0));
			}
			case Cursor.FIELD_TYPE_NULL: {
				return null;
			}
			case Cursor.FIELD_TYPE_STRING: {
				return (T) cursor.getString(0);
			}
		}

		return null;
	}
}
