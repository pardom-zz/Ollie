package ollie;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import com.example.ollie.model.NoteTag;
import ollie.internal.ModelAdapter;

public class NoteTag$$ModelAdapter extends ModelAdapter<NoteTag> {
	@Override
	public String getTableName() {
		return "NoteTags";
	}

	@Override
	public String getTableDefinition() {
		return "CREATE TABLE  IF NOT EXISTS " + getTableName() + " (" +
				BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"NoteId INTEGER, " +
				"TagId INTEGER, " +
				"FOREIGN KEY(NoteId) REFERENCES Note(" + BaseColumns._ID + "), " +
				"FOREIGN KEY(TagId) REFERENCES Tag(" + BaseColumns._ID + ")" +
				")";
	}

	@Override
	public void load(NoteTag entity, Cursor cursor) {
		entity.id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
		entity.note = Ollie.getOrFindEntity(entity.note.getClass(), cursor.getLong(cursor.getColumnIndex("NoteId")));
		entity.tag = Ollie.getOrFindEntity(entity.tag.getClass(), cursor.getLong(cursor.getColumnIndex("TagId")));
	}

	@Override
	public Long save(NoteTag entity, SQLiteDatabase db) {
		ContentValues values = new ContentValues();
		values.put("NotedId", entity.note.id);
		values.put("TagId", entity.tag.id);
		return insertOrUpdate(entity, db, values);
	}
}