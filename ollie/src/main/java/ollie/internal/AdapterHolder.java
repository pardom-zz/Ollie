package ollie.internal;

import ollie.Model;
import ollie.TypeAdapter;

import java.util.Set;

public interface AdapterHolder {
	public Set<? extends ModelAdapter> getModelAdapters();

	public <T extends Model> ModelAdapter<T> getModelAdapter(Class<? extends Model> cls);

	public <D, S extends TypeAdapter<D, S>> TypeAdapter<D, S> getTypeAdapter(Class<D> cls);
}