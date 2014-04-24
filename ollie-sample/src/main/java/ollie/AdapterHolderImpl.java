package ollie;

import ollie.internal.AdapterHolder;
import ollie.internal.ModelAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AdapterHolderImpl implements AdapterHolder {
	private static final Map<Class<? extends Model>, ModelAdapter> MODEL_ADAPTERS = new HashMap<Class<? extends Model>, ModelAdapter>() {
		{
			put(com.example.ollie.model.Note.class, new Note$$ModelAdapter());
			put(com.example.ollie.model.Tag.class, new Tag$$ModelAdapter());
			put(com.example.ollie.model.NoteTag.class, new NoteTag$$ModelAdapter());
		}
	};

	private static final Map<Class, TypeAdapter> TYPE_ADAPTERS = new HashMap<Class, TypeAdapter>() {
		{
			put(java.util.Date.class, new ollie.adapter.UtilDateAdapter());
			put(java.sql.Date.class, new ollie.adapter.SqlDateAdapter());
		}
	};

	@Override
	public Set<? extends ModelAdapter> getModelAdapters() {
		return new HashSet(MODEL_ADAPTERS.values());
	}

	@Override
	public <T extends Model> ModelAdapter<T> getModelAdapter(Class<? extends Model> cls) {
		return MODEL_ADAPTERS.get(cls);
	}

	@Override
	public <D, S extends TypeAdapter<D, S>> TypeAdapter<D, S> getTypeAdapter(Class<D> cls) {
		return TYPE_ADAPTERS.get(cls);
	}
}