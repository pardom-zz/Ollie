package ollie.internal;

import ollie.Migration;
import ollie.Model;
import ollie.TypeAdapter;

import java.util.List;


public interface AdapterHolder {
	public static final String IMPLEMENTATION_CLASS_PACKAGE = "ollie";
	public static final String IMPLEMENTATION_CLASS_NAME = "AdapterHolderImpl";
	public static final String IMPLEMENTATION_CLASS_FQCN = IMPLEMENTATION_CLASS_PACKAGE + "." + IMPLEMENTATION_CLASS_NAME;

	public List<? extends ModelAdapter> getModelAdapters();

	public List<? extends Migration> getMigrations();

	public <T extends Model> ModelAdapter<T> getModelAdapter(Class<? extends Model> cls);

	public <D, S extends TypeAdapter<D, S>> TypeAdapter<D, S> getTypeAdapter(Class<D> cls);
}