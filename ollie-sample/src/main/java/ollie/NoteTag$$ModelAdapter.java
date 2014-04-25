package ollie;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import ollie.internal.ModelAdapter;

public class NoteTag$$ModelAdapter extends ModelAdapter<com.example.ollie.model.NoteTag> {
	@Override
	public String getTableName() {
		return "NoteTags";
	}

	@Override
	public String getSchema() {
		return "CREATE TABLE  IF NOT EXISTS NoteTags (" +
				"_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"NoteId INTEGER, " +
				"TagId INTEGER, " +
				"FOREIGN KEY(NoteId) REFERENCES Note(_id), " +
				"FOREIGN KEY(TagId) REFERENCES Tag(_id)" +
				")";
	}

	@Override
	public void load(com.example.ollie.model.NoteTag entity, Cursor cursor) {
		entity.id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
		entity.note = Ollie.getOrFindEntity(entity.note.getClass(), cursor.getLong(cursor.getColumnIndex("NoteId")));
		entity.tag = Ollie.getOrFindEntity(entity.tag.getClass(), cursor.getLong(cursor.getColumnIndex("TagId")));
	}

	@Override
	public Long save(com.example.ollie.model.NoteTag entity, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put("NotedId", entity.note.id);
		values.put("TagId", entity.tag.id);
		return insertOrUpdate(entity, db, values);
	}
}