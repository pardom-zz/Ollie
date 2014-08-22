package ollie.internal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;


import ollie.Model;

public abstract class ModelAdapter<T extends Model> {
	public abstract Class<? extends Model> getModelType();

	public abstract String getTableName();

	public abstract String getSchema();

    public abstract String getTypeColumn();

    public abstract String getTypeName();

    public abstract T newEntity(String type);

	public abstract void load(T entity, Cursor cursor);

	public abstract Long save(T entity, SQLiteDatabase db);

	public abstract void delete(T entity, SQLiteDatabase db);


	protected final Long insertOrUpdate(T entity, SQLiteDatabase db, ContentValues values) {
        if (isPolymorphic()){
            values.put(getTypeColumn(), getTypeName());
        }
		if (entity.id == null) {
			entity.id = db.insert(getTableName(), null, values);
		} else {
			db.update(getTableName(), values, BaseColumns._ID + "=?", new String[]{entity.id.toString()});
		}

		return entity.id;
	}

    public final boolean isPolymorphic(){
        return getTypeColumn() != null && !getTypeColumn().isEmpty();
    }
}