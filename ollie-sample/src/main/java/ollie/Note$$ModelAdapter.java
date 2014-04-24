package ollie;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import com.example.ollie.model.Note;
import ollie.internal.ModelAdapter;

public class Note$$ModelAdapter extends ModelAdapter<Note> {
	@Override
	public String getTableName() {
		return "Notes";
	}

	@Override
	public String getTableDefinition() {
		return "CREATE TABLE  IF NOT EXISTS " + getTableName() + " (" +
				BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"Title TEXT, " +
				"Body TEXT, " +
				"Date INTEGER" +
				")";
	}

	@Override
	public void load(Note entity, Cursor cursor) {
		entity.id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
		entity.title = cursor.getString(cursor.getColumnIndex("Title"));
		entity.body = cursor.getString(cursor.getColumnIndex("Body"));
		entity.date = Ollie.getTypeAdapter(java.util.Date.class).deserialize(cursor.getLong(cursor.getColumnIndex("Date")));
	}

	@Override
	public Long save(Note entity, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put("Title", entity.title);
		values.put("Body", entity.body);
		values.put("Date", (Long) Ollie.getTypeAdapter(java.util.Date.class).serialize(entity.date));
		return insertOrUpdate(entity, db, values);
	}
}