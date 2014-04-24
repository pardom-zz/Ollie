package ollie;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import com.example.ollie.model.Tag;
import ollie.internal.ModelAdapter;

public class Tag$$ModelAdapter extends ModelAdapter<Tag> {
	@Override
	public String getTableName() {
		return "Tags";
	}

	@Override
	public String getTableDefinition() {
		return "CREATE TABLE  IF NOT EXISTS " + getTableName() + " (" +
				BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"Name TEXT" +
				")";
	}

	@Override
	public void load(Tag entity, Cursor cursor) {
		entity.id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
		entity.name = cursor.getString(cursor.getColumnIndex("Name"));
	}

	@Override
	public Long save(Tag entity, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put("Name", entity.name);
		return insertOrUpdate(entity, db, values);
	}
}