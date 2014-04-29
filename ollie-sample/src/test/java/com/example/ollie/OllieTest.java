package com.example.ollie;

import android.content.ContentProvider;
import com.example.ollie.content.OllieSampleProvider;
import com.example.ollie.model.Note;
import com.example.ollie.shadows.PersistentShadowSQLiteOpenHelper;
import ollie.Model;
import ollie.Ollie;
import ollie.query.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

import java.io.File;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, shadows = PersistentShadowSQLiteOpenHelper.class)
public class OllieTest {
	@BeforeClass
	public static void setup() {
		new File("path").delete();
	}

	@AfterClass
	public static void cleanUp() {
		new File("path").delete();
	}

	@Before
	public void initialize() {
		ContentProvider contentProvider = new OllieSampleProvider();
		contentProvider.onCreate();

		ShadowContentResolver.registerProvider("com.example.ollie", contentProvider);

		Ollie.init(Robolectric.application, "OllieSample.db", 1);
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
	public void testSelect() {
	}

	@Test
	public void testInsert() {
		String sql;
		Query query;

		sql = "INSERT INTO notes VALUES(?, ?, ?)";
		query = new Insert().into(Note.class).values("Testing INSERT", "Testing INSERT body.", "0");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(new String[]{"Testing INSERT", "Testing INSERT body.", "0"});

		sql = "INSERT INTO notes(title) VALUES(?)";
		query = new Insert().into(Note.class, "title").values("Testing INSERT");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(new String[]{"Testing INSERT"});

		sql = "INSERT INTO notes(title, body) VALUES(?, ?)";
		query = new Insert().into(Note.class, "title", "body").values("Testing INSERT", "Testing INSERT body.");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(new String[]{"Testing INSERT", "Testing INSERT body."});

		sql = "INSERT INTO notes(title, body, date) VALUES(?, ?, ?)";
		query = new Insert().into(Note.class, "title", "body", "date").values("Testing INSERT", "Testing INSERT body.", "0");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(new String[]{"Testing INSERT", "Testing INSERT body.", "0"});

		try {
			new Insert().into(Note.class, "title", "body", "date").values("Testing INSERT", "Testing INSERT body.").execute();
			assert false;
		} catch (MalformedQueryException e) {
			// Successfully threw exception
			assert true;
		}

	}

	@Test
	public void testUpdate() {
		String sql;
		Query query;

		sql = "UPDATE notes SET title='Testing UPDATE'";
		query = new Update(Note.class).set("title='Testing UPDATE'");
		assertThat(query.getSql()).isEqualTo(sql);

		sql = "UPDATE notes SET title=?";
		query = new Update(Note.class).set("title=?", "Testing UPDATE");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(new String[]{"Testing UPDATE"});

		sql = "UPDATE notes SET title='Testing UPDATE' WHERE _id=1";
		query = new Update(Note.class).set("title='Testing UPDATE'").where(Model._ID + "=1");
		assertThat(query.getSql()).isEqualTo(sql);

		sql = "UPDATE notes SET title=? WHERE _id=?";
		query = new Update(Note.class).set("title=?", "Testing UPDATE").where(Model._ID + "=?", "1");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(new String[]{"Testing UPDATE", "1"});
	}

	@Test
	public void testDelete() {
		String sql;
		Query query;

		sql = "DELETE FROM notes";
		query = new Delete().from(Note.class);
		assertThat(query.getSql()).isEqualTo(sql);

		sql = "DELETE FROM notes WHERE _id=1";
		query = new Delete().from(Note.class).where(Model._ID + "=1");
		assertThat(query.getSql()).isEqualTo(sql);

		sql = "DELETE FROM notes WHERE _id=?";
		query = new Delete().from(Note.class).where(Model._ID + "=?", "1");
		assertThat(query.getSql()).isEqualTo(sql);
		assertThat(query.getArgs()).isEqualTo(new String[]{"1"});
	}
}