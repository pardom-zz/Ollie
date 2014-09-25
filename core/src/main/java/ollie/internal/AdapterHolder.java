package ollie.internal;

import ollie.Migration;
import ollie.Model;
import ollie.TypeAdapter;

import java.util.List;


public interface AdapterHolder {
	public static final String IMPL_CLASS_PACKAGE = "ollie";
	public static final String IMPL_CLASS_NAME = "AdapterHolderImpl";
	public static final String IMPL_CLASS_FQCN = IMPL_CLASS_PACKAGE + "." + IMPL_CLASS_NAME;

	public List<? extends Migration> getMigrations();

	public <T extends Model> ModelAdapter<T> getModelAdapter(Class<? extends Model> cls);

	public List<? extends ModelAdapter> getModelAdapters();

	public <D, S> TypeAdapter<D, S> getTypeAdapter(Class<D> cls);
}