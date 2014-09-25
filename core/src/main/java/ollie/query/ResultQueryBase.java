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
	public <T> T fetchValueAs(Class<T> type) {
		final Cursor cursor = Ollie.getDatabase().rawQuery(getSql(), getArgs());
		if (!cursor.moveToFirst()) {
			return null;
		}

		if (type.equals(Byte[].class) || type.equals(byte[].class)) {
			return (T) cursor.getBlob(0);
		} else if (type.equals(double.class) || type.equals(Double.class)) {
			return (T) Double.valueOf(cursor.getDouble(0));
		} else if (type.equals(float.class) || type.equals(Float.class)) {
			return (T) Float.valueOf(cursor.getFloat(0));
		} else if (type.equals(int.class) || type.equals(Integer.class)) {
			return (T) Integer.valueOf(cursor.getInt(0));
		} else if (type.equals(long.class) || type.equals(Long.class)) {
			return (T) Long.valueOf(cursor.getLong(0));
		} else if (type.equals(short.class) || type.equals(Short.class)) {
			return (T) Short.valueOf(cursor.getShort(0));
		} else if (type.equals(String.class)) {
			return (T) cursor.getString(0);
		}

		return null;
	}
}
