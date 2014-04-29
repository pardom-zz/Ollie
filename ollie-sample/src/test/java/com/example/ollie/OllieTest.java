package com.example.ollie;

import android.content.ContentProvider;
import com.example.ollie.content.OllieSampleProvider;
import com.example.ollie.model.Note;
import com.example.ollie.shadows.PersistentShadowSQLiteOpenHelper;
import ollie.Ollie;
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
}