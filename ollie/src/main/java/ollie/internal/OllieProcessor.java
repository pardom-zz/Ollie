package ollie.internal;

import ollie.adapter.SqlDateAdapter;
import ollie.adapter.UtilDateAdapter;
import ollie.annotation.Column;
import ollie.annotation.Table;
import ollie.annotation.TypeAdapter;
import ollie.internal.ModelAdapterDefinition.ColumnDefinition;

import javax.annotation.processing.*;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.WARNING;

@SupportedAnnotationTypes({"*"})
public class OllieProcessor extends AbstractProcessor {
	private static final String MODEL_ADAPTER_SUFFIX = "$$ModelAdapter";
	private static final String MODEL_CLASS = "ollie.Model";
	private static final String TYPE_ADAPTER_CLASS = "ollie.TypeAdapter<?,?>";

	private static final Map<String, ModelAdapterDefinition> MODEL_ADAPTERS = new HashMap<String, ModelAdapterDefinition>();
	private static final Map<String, TypeAdapterDefinition> TYPE_ADAPTERS = new HashMap<String, TypeAdapterDefinition>();

	private static final Class[] DEFAULT_TYPE_ADAPTERS = new Class[]{
			SqlDateAdapter.class,
			UtilDateAdapter.class
	};

	private static final Map<String, String> SQL_TYPE_MAP = new HashMap<String, String>() {
		{
			put(byte[].class.getName(), "BLOB");
			put(Byte[].class.getName(), "BLOB");
			put(double.class.getName(), "REAL");
			put(Double.class.getName(), "REAL");
			put(float.class.getName(), "REAL");
			put(Float.class.getName(), "REAL");
			put(int.class.getName(), "INTEGER");
			put(Integer.class.getName(), "INTEGER");
			put(long.class.getName(), "INTEGER");
			put(Long.class.getName(), "INTEGER");
			put(short.class.getName(), "INTEGER");
			put(Short.class.getName(), "INTEGER");
			put(String.class.getName(), "TEXT");
		}
	};

	private Elements elementUtils;
	private Filer filer;

	@Override
	public synchronized void init(ProcessingEnvironment env) {
		super.init(env);
		elementUtils = env.getElementUtils();
		filer = env.getFiler();
	}

	@Override
	public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment env) {
		findAndParseTypeAdapters(env);
		findAndParseModels(env);
		writeModelAdapters();
		writeAdapterHolder();
		return true;
	}

	private void writeModelAdapters() {
		for (Map.Entry<String, ModelAdapterDefinition> entry : MODEL_ADAPTERS.entrySet()) {
			String model = entry.getKey();
			ModelAdapterDefinition definition = entry.getValue();
			Element element = elementUtils.getTypeElement(model);

			try {
				JavaFileObject jfo = filer.createSourceFile(definition.getFqcn(), null);
				Writer writer = jfo.openWriter();
				writer.write(definition.brewJava());
				writer.flush();
				writer.close();
			} catch (IOException e) {
				warning(element, "Unable to write adapter for type %s: %s", element, e.getMessage());
			}
		}
	}

	private void writeAdapterHolder() {
		StringBuilder builder = new StringBuilder();
		builder.append("// Generated code from Ollie. Do not modify!\n");
		builder.append("package ollie;\n\n");
		builder.append("import ollie.internal.AdapterHolder;\n");
		builder.append("import ollie.internal.ModelAdapter;\n\n");
		builder.append("import java.util.HashMap;\n");
		builder.append("import java.util.HashSet;\n");
		builder.append("import java.util.Map;\n");
		builder.append("import java.util.Set;\n\n");
		builder.append("public class ").append(AdapterHolder.IMPLEMENTATION_CLASS_NAME).append(" implements AdapterHolder {\n");
		builder.append("	private static final Map<Class<? extends Model>, ModelAdapter> MODEL_ADAPTERS = new HashMap<Class<? extends Model>, ModelAdapter>() {\n");
		builder.append("		{\n");

		for (Map.Entry<String, ModelAdapterDefinition> entry : MODEL_ADAPTERS.entrySet()) {
			String model = entry.getKey();
			ModelAdapterDefinition definition = entry.getValue();
			builder.append("			put(").append(model).append(".class, new ollie.").append(definition.getClassName()).append("());\n");
		}

		builder.append("		}\n");
		builder.append("	};\n\n");
		builder.append("	private static final Map<Class, TypeAdapter> TYPE_ADAPTERS = new HashMap<Class, TypeAdapter>() {\n");
		builder.append("		{\n");

		for (Map.Entry<String, TypeAdapterDefinition> entry : TYPE_ADAPTERS.entrySet()) {
			String model = entry.getKey();
			TypeAdapterDefinition definition = entry.getValue();
			builder.append("			put(").append(model).append(".class, new ").append(definition.getFqcn()).append("());\n");
		}

		builder.append("		}\n");
		builder.append("	};\n\n");
		builder.append("	@Override\n");
		builder.append("	public Set<? extends ModelAdapter> getModelAdapters() {\n");
		builder.append("		return new HashSet(MODEL_ADAPTERS.values());\n");
		builder.append("	}\n\n");
		builder.append("	@Override\n");
		builder.append("	public <T extends Model> ModelAdapter<T> getModelAdapter(Class<? extends Model> cls) {\n");
		builder.append("		return MODEL_ADAPTERS.get(cls);\n");
		builder.append("	}\n\n");
		builder.append("	@Override\n");
		builder.append("	public <D, S extends ollie.TypeAdapter<D, S>> ollie.TypeAdapter<D, S> getTypeAdapter(Class<D> cls) {\n");
		builder.append("		return TYPE_ADAPTERS.get(cls);\n");
		builder.append("	}\n");
		builder.append("}");

		try {
			JavaFileObject jfo = filer.createSourceFile(AdapterHolder.IMPLEMENTATION_CLASS_FQCN, null);
			Writer writer = jfo.openWriter();
			writer.write(builder.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			warning(null, "Unable to write adapter holder for.");
		}
	}

	private void findAndParseTypeAdapters(RoundEnvironment env) {
		Set<Element> elements = new HashSet<Element>(env.getElementsAnnotatedWith(TypeAdapter.class));
		for (Class cls : DEFAULT_TYPE_ADAPTERS) {
			elements.add(elementUtils.getTypeElement(cls.getName()));
		}

		for (Element element : elements) {
			parseTypeAdapterAnnotation(element);
		}
	}

	private void findAndParseModels(RoundEnvironment env) {
		for (Element element : env.getElementsAnnotatedWith(Table.class)) {
			parseTableAnnotation(element);
		}
	}

	private void findAndParseColumns(TypeMirror typeMirror, Set<ColumnDefinition> definitions) {
		DeclaredType declaredType = (DeclaredType) typeMirror;
		Element element = declaredType.asElement();
		TypeElement typeElement = (TypeElement) element;

		if (isSubtypeOfType(typeElement.getSuperclass(), MODEL_CLASS)) {
			findAndParseColumns(typeElement.getSuperclass(), definitions);
		}

		for (Element enclosedElement : element.getEnclosedElements()) {
			Column column = enclosedElement.getAnnotation(Column.class);
			if (column == null) {
				continue;
			}

			String name = column.value();
			String targetName = enclosedElement.toString();
			String deserializedType = enclosedElement.asType().toString();
			String serializedType = deserializedType;
			String sqlType;
			boolean isModel = false;

			if (isSubtypeOfType(enclosedElement.asType(), MODEL_CLASS)) {
				isModel = true;
				serializedType = Long.class.getName();
			}
			else if (TYPE_ADAPTERS.containsKey(deserializedType)) {
				serializedType = TYPE_ADAPTERS.get(deserializedType).getSerializedType();
			}

			sqlType = SQL_TYPE_MAP.get(serializedType);

			if (sqlType == null) {
				error(enclosedElement, "@Column type contains no SQL type mapping. (%s)", deserializedType);
				continue;
			}

			ColumnDefinition columnDefinition = new ColumnDefinition();
			columnDefinition.setName(name);
			columnDefinition.setTargetName(targetName);
			columnDefinition.setDeserializedType(deserializedType);
			columnDefinition.setSerializedType(serializedType);
			columnDefinition.setSqlType(sqlType);
			columnDefinition.setIsModel(isModel);

			for (AnnotationMirror annotationMirror : enclosedElement.getAnnotationMirrors()) {
				try {
					Class annotationClass = Class.forName(annotationMirror.getAnnotationType().toString());
					columnDefinition.putAnnotation(annotationClass, enclosedElement.getAnnotation(annotationClass));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}

			definitions.add(columnDefinition);
		}
	}

	private void parseTypeAdapterAnnotation(Element element) {
		boolean hasError = false;

		Element enclosingElement = element.getEnclosingElement();
		String classPackage = enclosingElement.toString();
		String className = element.getSimpleName().toString();

		// Verify that the target class extends from TypeAdapter.
		if (!isSubtypeOfType(element.asType(), TYPE_ADAPTER_CLASS)) {
			error(element, "@TypeAdapter classes must extend from TypeAdapter. (%s.%s)", classPackage, className);
			hasError = true;
		}

		TypeElement typeElement = (TypeElement) element;
		TypeMirror superType = typeElement.getSuperclass();
		DeclaredType declaredType = (DeclaredType) superType;
		List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();

		String deserializedType = typeArguments.get(0).toString();
		String serializedType = typeArguments.get(1).toString();

		TypeAdapterDefinition typeAdapter = new TypeAdapterDefinition();
		typeAdapter.setClassPackage(classPackage);
		typeAdapter.setClassName(className);
		typeAdapter.setDeserializedType(deserializedType);
		typeAdapter.setSerializedType(serializedType);

		TYPE_ADAPTERS.put(deserializedType, typeAdapter);
	}

	private void parseTableAnnotation(Element element) {
		boolean hasError = false;

		Element enclosingElement = element.getEnclosingElement();
		String classPackage = enclosingElement.toString();
		String className = element.getSimpleName().toString();
		String targetType = element.toString();
		String tableName = element.getAnnotation(Table.class).value();

		// Verify that the target class extends from Model.
		if (!isSubtypeOfType(element.asType(), MODEL_CLASS)) {
			error(element, "@Table classes must extend from Model. (%s.%s)", classPackage, className);
			hasError = true;
		}

		Set<ColumnDefinition> columnDefinitions = new LinkedHashSet<ColumnDefinition>();
		findAndParseColumns(element.asType(), columnDefinitions);

		ModelAdapterDefinition definition = new ModelAdapterDefinition();
		definition.setClassPackage("ollie");
		definition.setClassName(className + MODEL_ADAPTER_SUFFIX);
		definition.setTargetType(targetType);
		definition.setColumnDefinitions(columnDefinitions);
		definition.setTableName(tableName);

		MODEL_ADAPTERS.put(targetType, definition);
	}

	private boolean isSubtypeOfType(TypeMirror typeMirror, String otherType) {
		if (otherType.equals(typeMirror.toString())) {
			return true;
		}
		if (!(typeMirror instanceof DeclaredType)) {
			return false;
		}
		DeclaredType declaredType = (DeclaredType) typeMirror;
		List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
		if (typeArguments.size() > 0) {
			StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
			typeString.append('<');
			for (int i = 0; i < typeArguments.size(); i++) {
				if (i > 0) {
					typeString.append(',');
				}
				typeString.append('?');
			}
			typeString.append('>');
			if (typeString.toString().equals(otherType)) {
				return true;
			}
		}
		Element element = declaredType.asElement();
		if (!(element instanceof TypeElement)) {
			return false;
		}
		TypeElement typeElement = (TypeElement) element;
		TypeMirror superType = typeElement.getSuperclass();
		if (isSubtypeOfType(superType, otherType)) {
			return true;
		}
		for (TypeMirror interfaceType : typeElement.getInterfaces()) {
			if (isSubtypeOfType(interfaceType, otherType)) {
				return true;
			}
		}
		return false;
	}

	private void warning(Element element, String message, Object... args) {
		if (args.length > 0) {
			message = String.format(message, args);
		}
		processingEnv.getMessager().printMessage(WARNING, message, element);
	}

	private void error(Element element, String message, Object... args) {
		if (args.length > 0) {
			message = String.format(message, args);
		}
		processingEnv.getMessager().printMessage(ERROR, message, element);
	}
}