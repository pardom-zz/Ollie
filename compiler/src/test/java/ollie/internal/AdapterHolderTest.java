package ollie.internal;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static ollie.internal.ProcessorTestUtilities.ollieProcessors;
import static org.truth0.Truth.ASSERT;

public class AdapterHolderTest {
	@Test
	public void migrations() {
		JavaFileObject source = JavaFileObjects.forSourceLines("ollie.test.AddDateColumnMigration",
				"package ollie.test;",

				"import ollie.Migration;",

				"public class AddDateColumnMigration extends ollie.Migration {",
				"	@Override",
				"	public int getVersion() {",
				"		return 2;",
				"	}",

				"	@Override",
				"	public String[] getStatements() {",
				"		return new String[]{",
				"				\"ALTER TABLE notes ADD COLUMN date INTEGER\"",
				"		};",
				"	}",
				"}"
		);

		JavaFileObject adapterHolderImpl = JavaFileObjects.forSourceLines("ollie/AdapterHolderImpl",
				"package ollie;",

				"import java.util.ArrayList;",
				"import java.util.HashMap;",
				"import java.util.List;",
				"import java.util.Map;",
				"import ollie.internal.AdapterHolder;",
				"import ollie.internal.ModelAdapter;",

				"public final class AdapterHolderImpl implements AdapterHolder {",
				"	private static final List<Migration> MIGRATIONS = new ArrayList<Migration>();",
				"	private static final Map<Class<? extends Model>, ModelAdapter> MODEL_ADAPTERS = " +
						"new HashMap<Class<? extends Model>, ModelAdapter>();",
				"	private static final Map<Class, TypeAdapter> TYPE_ADAPTERS = new HashMap<Class, TypeAdapter>();",

				"	static {",
				"		MIGRATIONS.add(new ollie.test.AddDateColumnMigration());",

				"		TYPE_ADAPTERS.put(java.lang.Boolean.class, new ollie.adapter.BooleanAdapter());",
				"		TYPE_ADAPTERS.put(java.util.Calendar.class, new ollie.adapter.CalendarAdapter());",
				"		TYPE_ADAPTERS.put(java.sql.Date.class, new ollie.adapter.SqlDateAdapter());",
				"		TYPE_ADAPTERS.put(java.util.Date.class, new ollie.adapter.UtilDateAdapter());",
				"	}",

				"	public final List<? extends Migration> getMigrations() {",
				"		return MIGRATIONS;",
				"	}",

				"	public final <T extends Model> ModelAdapter<T> getModelAdapter(Class<? extends Model> cls) {",
				"		return MODEL_ADAPTERS.get(cls);",
				"	}",

				"	public final List<? extends ModelAdapter> getModelAdapters() {",
				"		return new ArrayList(MODEL_ADAPTERS.values());",
				"	}",

				"	public final <D, S> TypeAdapter<D, S> getTypeAdapter(Class<D> cls) {",
				"		return TYPE_ADAPTERS.get(cls);",
				"	}",
				"}"
		);

		ASSERT.about(javaSource()).that(source)
				.processedWith(ollieProcessors())
				.compilesWithoutError()
				.and()
				.generatesSources(adapterHolderImpl);
	}
}
