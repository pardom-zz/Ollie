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

import android.content.ContentProvider;
import ollie.Model;
import ollie.Ollie;
import ollie.query.*;
import ollie.test.content.OllieSampleProvider;
import ollie.test.model.Note;
import ollie.test.model.NoteTag;
import ollie.test.model.Tag;
import ollie.test.shadows.PersistentShadowSQLiteOpenHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.shadows.ShadowLog;
import rx.functions.Action1;

import java.io.File;
import java.util.*;

import static ollie.Ollie.LogLevel;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, shadows = PersistentShadowSQLiteOpenHelper.class)
public class OllieTest {
	private static final int NOTE_COUNT = 100;
	private static final int TAG_COUNT = 10;

	@BeforeClass
	public static void setup() {
		new File("path").delete();
	}

	@Before
	public void initialize() {
		ContentProvider contentProvider = new OllieSampleProvider();
		contentProvider.onCreate();

		ShadowLog.stream = System.out;
		ShadowContentResolver.registerProvider("com.example.ollie", contentProvider);

		Ollie.with(Robolectric.application)
				.setName("OllieSample.db")
				.setLogLevel(LogLevel.FULL)
				.init();
	}

	@Test
	public void testPopulateDatabase() {
		final Tag[] tags = new Tag[TAG_COUNT];
		final Random rand = new Random();

		for (int i = 0; i < TAG_COUNT; i++) {
			Tag tag = new Tag();
			tag.name = "Tag " + i;
			tag.save();

			tags[i] = tag;
		}

		for (int i = 0; i < NOTE_COUNT; i++) {
			Note note = new Note();
			note.title = "Note " + i;
			note.body = "This is the body for note #" + i;
			note.date = new Date();
			note.save();

			final int tagCount = rand.nextInt(TAG_COUNT);
			final List<Tag> tagList = new ArrayList<Tag>(Arrays.asList(tags));
			Collections.shuffle(tagList);

			for (int j = 0; j < tagCount; j++) {
				NoteTag noteTag = new NoteTag();
				noteTag.note = note;
				noteTag.tag = tagList.remove(0);
				noteTag.save();
			}
		}
	}

	@Test
	public void testSaveEntity() {
		Note note = new Note();
		assertThat(note.id).isNull();

		note.title = "Test note";
		note.body = "Testing saving a note.";
		note.save();
		assertThat(note.id).isNotNull();
		assertThat(note.id).isGreaterThan(0l);
	}

	@Test
	public void testLoadEntity() {
		Note note = Note.find(Note.class, 1l);
		assertThat(note).isNotNull();
		assertThat(note.id).isNotNull();
		assertThat(note.id).isGreaterThan(0l);
	}

	@Test
	public void testDeleteEntity() {
		Note note = Note.find(Note.class, 1l);
		assertThat(note).isNotNull();
		assertThat(note.id).isNotNull();
		assertThat(note.id).isGreaterThan(0l);

		note.delete();
		assertThat(note).isNotNull();
		assertThat(note.id).isNull();
	}

	@Test
	public void testSelectSql() {
		String sql;
		Query query;

		sql = "SELECT notes.* FROM notes";
		query = Select.from(Note.class);
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(null);

		sql = "SELECT notes.* FROM notes WHERE _id=?";
		query = Select.from(Note.class).where(Model._ID + "=?", "1");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(new String[]{"1"});

		sql = "SELECT notes.* FROM notes ORDER BY title ASC";
		query = Select.from(Note.class).orderBy("title ASC");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(null);

		sql = "SELECT notes.* FROM notes LIMIT 1";
		query = Select.from(Note.class).limit("1");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(null);

		sql = "SELECT notes.* FROM notes LIMIT 1 OFFSET 10";
		query = Select.from(Note.class).limit("1").offset("10");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(null);

		sql = "SELECT notes.* " +
				"FROM notes " +
				"INNER JOIN noteTags ON notes.id=noteTags.note " +
				"INNER JOIN tags ON tag.id=noteTags.tag " +
				"WHERE tag.name=? " +
				"ORDER BY notes.title ASC " +
				"LIMIT 10 " +
				"OFFSET 10";
		query = Select
				.from(Note.class)
				.innerJoin(NoteTag.class).on("notes.id=noteTags.note")
				.innerJoin(Tag.class).on("tag.id=noteTags.tag")
				.where("tag.name=?", "test")
				.orderBy("notes.title ASC")
				.limit("10")
				.offset("10");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(new String[]{"test"});
	}

	@Test
	public void testInsertSql() {
		String sql;
		Query query;

		sql = "INSERT INTO notes VALUES(?, ?, ?)";
		query = Insert.into(Note.class).values("Testing INSERT", "Testing INSERT body.", "0");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(new String[]{"Testing INSERT", "Testing INSERT body.", "0"});

		sql = "INSERT INTO notes (title) VALUES(?)";
		query = Insert.into(Note.class).columns("title").values("Testing INSERT");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(new String[]{"Testing INSERT"});

		sql = "INSERT INTO notes (title, body) VALUES(?, ?)";
		query = Insert.into(Note.class).columns("title", "body").values("Testing INSERT", "Testing INSERT body.");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(new String[]{"Testing INSERT", "Testing INSERT body."});

		sql = "INSERT INTO notes (title, body, date) VALUES(?, ?, ?)";
		query = Insert.into(Note.class).columns("title", "body", "date").values("Testing INSERT",
				"Testing INSERT body.", "0");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(new String[]{"Testing INSERT", "Testing INSERT body.", "0"});

		try {
			Insert.into(Note.class).columns("title", "body", "date")
					.values("Testing INSERT", "Testing INSERT body.")
					.execute();
			assert false;
		} catch (Query.MalformedQueryException e) {
			// Successfully threw exception
			assert true;
		}

	}

	@Test
	public void testUpdateSql() {
		String sql;
		Query query;

		sql = "UPDATE notes SET title='Testing UPDATE'";
		query = Update.table(Note.class).set("title='Testing UPDATE'");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(null);

		sql = "UPDATE notes SET title=?";
		query = Update.table(Note.class).set("title=?", "Testing UPDATE");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(new String[]{"Testing UPDATE"});

		sql = "UPDATE notes SET title='Testing UPDATE' WHERE _id=1";
		query = Update.table(Note.class).set("title='Testing UPDATE'").where(Model._ID + "=1");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(null);

		sql = "UPDATE notes SET title=? WHERE _id=?";
		query = Update.table(Note.class).set("title=?", "Testing UPDATE").where(Model._ID + "=?", "1");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(new String[]{"Testing UPDATE", "1"});
	}

	@Test
	public void testDeleteSql() {
		String sql;
		Query query;

		sql = "DELETE FROM notes";
		query = Delete.from(Note.class);
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(null);

		sql = "DELETE FROM notes WHERE _id=1";
		query = Delete.from(Note.class).where(Model._ID + "=1");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(null);

		sql = "DELETE FROM notes WHERE _id=?";
		query = Delete.from(Note.class).where(Model._ID + "=?", "1");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(new String[]{"1"});
	}

	@Test
	public void testSelectEntity() {
		// Single note
		Note note = Select.from(Note.class).fetchSingle();
		assertThat(note).isNotNull();
		assertThat(note.id).isNotNull();
		assertThat(note.id).isGreaterThan(0l);

		// Single note async
		Select.from(Note.class).observableSingle()
				.subscribe(new Action1<Note>() {
					@Override
					public void call(Note note) {
						System.out.println("2");
						assertThat(note).isNotNull();
						assertThat(note.id).isNotNull();
						assertThat(note.id).isGreaterThan(0l);
					}
				});


		// Single note by id
		note = Select.from(Note.class).where(Note._ID + "=?", 1).fetchSingle();
		assertThat(note).isNotNull();
		assertThat(note.id).isNotNull();
		assertThat(note.id).isGreaterThan(0l);

		// Single tag
		Tag tag = Select.from(Tag.class).fetchSingle();
		assertThat(tag).isNotNull();
		assertThat(tag.id).isNotNull();
		assertThat(tag.id).isGreaterThan(0l);

		// Save note tag to get guaranteed join result
		NoteTag noteTag = new NoteTag();
		noteTag.note = note;
		noteTag.tag = tag;
		noteTag.save();

		// Many
		List<Note> notes = Select.from(Note.class).fetch();
		assertThat(notes).isNotNull();
		assertThat(notes.size()).isGreaterThan(0);

		// Join
		notes = Select
				.columns("notes.*")
				.from(Note.class)
				.innerJoin(NoteTag.class).on("notes._id=noteTags.note")
				.innerJoin(Tag.class).on("tags._id=noteTags.tag")
				.where("tags._id=?", tag.id.toString())
				.fetch();
		assertThat(notes).isNotNull();
		assertThat(notes.size()).isGreaterThan(0);
	}

	@Test
	public void testFetchValue() {
		long sum = Select.columns("SUM(date)").from(Note.class).fetchValue(long.class);
		assertThat(sum).isGreaterThan(0);

		int count = Select.columns("COUNT(*)").from(Note.class).fetchValue(int.class);
		assertThat(count).isGreaterThan(0);
	}

	@Test
	public void testSaveNoteTagWithoutTag() {
		NoteTag noteTag = new NoteTag();
		noteTag.save();
		assertThat(noteTag.id).isGreaterThan(0l);
	}

	@Test
	public void testDeleteByQuery() {
		Note note = new Note();
		note.body = "this is draft";
		note.save();
		Delete.from(Note.class).where(Note._ID + "=?", note.id.toString()).execute();

		// TODO: This seems like a bit of work
		// assertThat(note.id).isNull();
	}
}