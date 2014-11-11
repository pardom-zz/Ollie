/*
 * Copyright (C) 2014 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ollie.test;

import ollie.Ollie;
import ollie.test.model.Note;
import ollie.test.model.Notebook;
import ollie.test.shadows.PersistentShadowSQLiteOpenHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.io.File;

import static ollie.Ollie.*;
import static org.assertj.core.api.Assertions.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, shadows = PersistentShadowSQLiteOpenHelper.class)
public class OllieEntityCacheTest {

	// Really small cache size to easily reproduce what happens when trying
	// loading data that isn't already in the cache
	private static final int SMALL_CACHE_SIZE = 1;

	@BeforeClass
	public static void setup() throws Exception {
		new File("path").delete();
		new File("path").createNewFile();
	}

	@Before
	public void initialize() {
		ShadowLog.stream = System.out;

		Ollie.init(Robolectric.application, "OllieSample.db", 1, SMALL_CACHE_SIZE, LogLevel.FULL);
	}

	@Test
	public void testSaveAndLoadEntity() {
		Note note = new Note();
		note.title = "Test note";
		note.body = "Testing saving a note.";
		note.save();
		Long noteId = note.id;
		assertThat(noteId).isNotNull();
		assertThat(noteId).isGreaterThan(0L);

		Notebook notebook1 = new Notebook();
		notebook1.name = "My Notebook 1";
		notebook1.note = note;
		notebook1.save();

		Long notebook1Id = notebook1.id;
		assertThat(notebook1Id).isNotNull();
		assertThat(notebook1Id).isGreaterThan(0L);

		// Everything is good because there is only one item in the LRU cache
		Notebook notebook1FromDB = Notebook.find(Notebook.class, notebook1Id);
		assertThat(notebook1FromDB.note).isNotNull();
		assertThat(notebook1FromDB.note.id).isEqualTo(noteId);

		// Create a new notebook which will knock notebook1 out of the LRU cache
		Notebook notebook2 = new Notebook();
		notebook2.name = "My Notebook 2";
		notebook2.note = note;
		notebook2.save();

		Long notebook2Id = notebook2.id;
		assertThat(notebook2Id).isNotNull();
		assertThat(notebook2Id).isGreaterThan(0L);

		// Everything is good because 'notebook2' is in the LRU cache now
		Notebook notebook2FromDB = Notebook.find(Notebook.class, notebook2Id);
		assertThat(notebook2FromDB.note).isNotNull();
		assertThat(notebook2FromDB.note.id).isEqualTo(noteId);

		/*
		 * Now let's look up notebook1 again, was failing because we read from the entity cache.
		 * notebook1 was not in the entity cache so when it tries to read from the entity.note field you get an
		 * NPE. I have pasted the bad generated file below.
		 *

		public final class Notebook$$ModelAdapter extends ModelAdapter<Notebook> {
			public final Class<? extends Model> getModelType() {
				return Notebook.class;
			}

			public final String getTableName() {
				return "notebooks";
			}

			public final String getSchema() {
				return "CREATE TABLE IF NOT EXISTS notebooks (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, note INTEGER)";
			}

			public final void load(Notebook entity, Cursor cursor) {
				entity.id = cursor.getLong(cursor.getColumnIndex("_id"));
				entity.name = cursor.getString(cursor.getColumnIndex("name"));
				entity.note = Ollie.getOrFindEntity(entity.note.getClass(), cursor.getLong(cursor.getColumnIndex("note")));  // This is the NPE
			}

			public final Long save(Notebook entity, SQLiteDatabase db) {
				ContentValues values = new ContentValues();
				values.put("_id", entity.id);
				values.put("name", entity.name);
				values.put("note", entity.note != null ? entity.note.id : null);
				return insertOrUpdate(entity, db, values);
			}

			public final void delete(Notebook entity, SQLiteDatabase db) {
				db.delete("notebooks", "_id=?", new String[]{entity.id.toString()});
			}
		}
		*/

		Notebook notebook1FromDBAgain = Notebook.find(Notebook.class, notebook1Id);
		assertThat(notebook1FromDBAgain.note).isNotNull();
		assertThat(notebook1FromDBAgain.note.id).isEqualTo(noteId);
	}
}