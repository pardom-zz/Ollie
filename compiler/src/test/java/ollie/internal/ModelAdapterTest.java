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

package ollie.internal;

import com.google.testing.compile.JavaFileObjects;
import ollie.internal.codegen.Errors;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static ollie.internal.ProcessorTestUtilities.ollieProcessors;
import static org.truth0.Truth.ASSERT;

public class ModelAdapterTest {
	@Test
	public void modelAdapter() {
		JavaFileObject source = JavaFileObjects.forSourceLines("ollie.test.Note",
				"package ollie.test;",

				"import java.util.Date;",
				"import ollie.Model;",
				"import ollie.annotation.Column;",
				"import ollie.annotation.NotNull;",
				"import ollie.annotation.Table;",

				"@Table(\"notes\")",
				"public class Note extends Model {",
				"	public static final String TITLE = \"title\";",
				"	public static final String BODY = \"body\";",
				"	public static final String DATE = \"date\";",
				"	@Column(TITLE) public String title;",
				"	@Column(BODY) @NotNull public String body;",
				"	@Column(DATE) public Date date;",
				"}"
		);

		JavaFileObject expectedSource = JavaFileObjects.forSourceLines("ollie/Note$$ModelAdapter",
				"package ollie;",

				"import android.content.ContentValues;",
				"import android.database.Cursor;",
				"import android.database.sqlite.SQLiteDatabase;",
				"import ollie.internal.ModelAdapter;",
				"import ollie.test.Note;",

				"public final class Note$$ModelAdapter extends ModelAdapter<Note> {",
				"	public final Class<? extends Model> getModelType() {",
				"		return Note.class;",
				"	}",

				"	public final String getTableName() {",
				"		return \"notes\";",
				"	}",

				"	public final String getSchema() {",
				"		return \"CREATE TABLE IF NOT EXISTS notes (\" +",
				"			\"_id INTEGER PRIMARY KEY AUTOINCREMENT, \" +",
				"			\"title TEXT, \" +",
				"			\"body TEXT NOT NULL, \" +",
				"			\"date INTEGER)\";",
				"	}",

				"	public final void load(Note entity, Cursor cursor) {",
				"		final int _idIndex = cursor.getColumnIndex(\"_id\");",
				"		final int titleIndex = cursor.getColumnIndex(\"title\")",
				"		final int bodyIndex = cursor.getColumnIndex(\"body\")",
				"		final int dateIndex = cursor.getColumnIndex(\"date\")",
				"		entity.id = _idIndex >= 0 ? cursor.getLong(_idIndex) : null;",
				"		entity.title = titleIndex >= 0 ? cursor.getString(titleIndex) : null;",
				"		entity.body = bodyIndex >= 0 ? cursor.getString(bodyIndex) : null;",
				"		entity.date = dateIndex >= 0 ? Ollie.getTypeAdapter(java.util.Date.class)",
				"				.deserialize(cursor.getLong(dateIndex)) : null;",
				"	}",

				"	public final Long save(Note entity, SQLiteDatabase db) {",
				"		ContentValues values = toContentValues(entity);",
				"		return insertOrUpdate(entity, db, values);",
				"	}",


				"	public final Long replace(Note entity, SQLiteDatabase db) {",
				"		ContentValues values = toContentValues(entity);",
				"		return db.replace(getTableName(), null, values);",
				"	}",

				"	public final ContentValues toContentValues(Note entity) {",
				"		ContentValues values = new ContentValues();",
				"		values.put(\"_id\", entity.id);",
				"		values.put(\"title\", entity.title);",
				"		values.put(\"body\", entity.body);",
				"		values.put(\"date\", (java.lang.Long) Ollie.getTypeAdapter(java.util.Date.class)",
				"				.serialize(entity.date));",
				"		return values;",
				"	}",

				"	public final void delete(Note entity, SQLiteDatabase db) {",
				"		db.delete(\"notes\", \"_id=?\", new String[]{entity.id.toString()});",
				"	}",
				"}"
		);

		ASSERT.about(javaSource()).that(source)
				.processedWith(ollieProcessors())
				.compilesWithoutError()
				.and()
				.generatesSources(expectedSource);
	}

	@Test
	public void tablesAreClasses() {
		JavaFileObject source = JavaFileObjects.forSourceLines("ollie.test.Note",
				"package ollie.test;",

				"import java.util.Date;",
				"import ollie.Model;",
				"import ollie.annotation.Column;",
				"import ollie.annotation.NotNull;",
				"import ollie.annotation.Table;",

				"@Table(\"notes\")",
				"public class Note extends Model {",
				"	public static final String TITLE = \"title\";",
				"	public static final String BODY = \"body\";",
				"	public static final String DATE = \"date\";",
				"	@Table(TITLE) public String title;",
				"	@Table(BODY) @NotNull public String body;",
				"	@Table(DATE) public Date date;",
				"}"
		);

		ASSERT.about(javaSource()).that(source)
				.processedWith(ollieProcessors())
				.failsToCompile();
	}

	@Test
	public void columnsAreFields() {
		JavaFileObject source = JavaFileObjects.forSourceLines("ollie.test.Note",
				"package ollie.test;",

				"import java.util.Date;",
				"import ollie.Model;",
				"import ollie.annotation.Column;",
				"import ollie.annotation.NotNull;",
				"import ollie.annotation.Table;",

				"@Column(\"notes\")",
				"public class Note extends Model {",
				"	public static final String TITLE = \"title\";",
				"	public static final String BODY = \"body\";",
				"	public static final String DATE = \"date\";",
				"	@Column(TITLE) public String title;",
				"	@Column(BODY) @NotNull public String body;",
				"	@Column(DATE) public Date date;",
				"}"
		);

		ASSERT.about(javaSource()).that(source)
				.processedWith(ollieProcessors())
				.failsToCompile()
				.withErrorContaining(Errors.COLUMN_TYPE_ERROR);
	}

	@Test
	public void columnsAreUnique() {
		JavaFileObject source = JavaFileObjects.forSourceLines("ollie.test.Note",
				"package ollie.test;",

				"import java.util.Date;",
				"import ollie.Model;",
				"import ollie.annotation.Column;",
				"import ollie.annotation.NotNull;",
				"import ollie.annotation.Table;",

				"@Table(\"notes\")",
				"public class Note extends Model {",
				"	public static final String TITLE = \"title\";",
				"	public static final String DATE = \"date\";",
				"	@Column(TITLE) public String title;",
				"	@Column(TITLE) @NotNull public String body;",
				"	@Column(DATE) public Date date;",
				"}"
		);

		ASSERT.about(javaSource()).that(source)
				.processedWith(ollieProcessors())
				.failsToCompile()
				.withErrorContaining(Errors.COLUMN_DUPLICATE_ERROR);
	}
}
