package ollie.internal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import ollie.Model;

public abstract class ModelAdapter<T extends Model> {
	public abstract String getTableName();

	public abstract String getSchema();

	public abstract void load(T entity, Cursor cursor);

	public abstract Long save(T entity, SQLiteDatabase db);

	protected final Long insertOrUpdate(T entity, SQLiteDatabase db, ContentValues values) {
		if (entity.id == null) {
			entity.id = db.insert(getTableName(), null, values);
		}
		else {
			db.update(getTableName(), values, "WHERE " + BaseColumns._ID + "=?", new String[]{entity.id.toString()});
		}

		return entity.id;
	}
}