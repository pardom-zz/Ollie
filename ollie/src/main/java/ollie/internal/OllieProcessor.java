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
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static javax.tools.Diagnostic.Kind.ERROR;

@SupportedAnnotationTypes({"*"})
public class OllieProcessor extends AbstractProcessor {
	public static final String SUFFIX = "$$ModelAdapter";
	static final String MODEL_CLASS = "ollie.Model";
	static final String TYPE_ADAPTER_CLASS = "ollie.TypeAdapter<?,?>";

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
	private Types typeUtils;
	private Filer filer;

	@Override
	public synchronized void init(ProcessingEnvironment env) {
		super.init(env);
		elementUtils = env.getElementUtils();
		typeUtils = env.getTypeUtils();
		filer = env.getFiler();
	}

	@Override
	public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment env) {
		findAndParseTypeAdapters(env);

		Map<Element, ModelAdapterDefinition> definitions = findAndParseModels(env);
		for (Map.Entry<Element, ModelAdapterDefinition> entry : definitions.entrySet()) {
			Element element = entry.getKey();
			ModelAdapterDefinition definition = entry.getValue();

			try {
				JavaFileObject jfo = filer.createSourceFile(definition.getFqcn(), element);
				Writer writer = jfo.openWriter();
				writer.write(definition.brewJava());
				writer.flush();
				writer.close();
			} catch (IOException e) {
				error(element, "Unable to write adapter for type %s: %s", element, e.getMessage());
			}
		}

		return true;
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

	private Map<Element, ModelAdapterDefinition> findAndParseModels(RoundEnvironment env) {
		Map<Element, ModelAdapterDefinition> definitions = new LinkedHashMap<Element, ModelAdapterDefinition>();
		for (Element element : env.getElementsAnnotatedWith(Table.class)) {
			parseTableAnnotation(element, definitions);
		}
		return definitions;
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
			String targetType = enclosedElement.asType().toString();
			String sqlType;

			if (isSubtypeOfType(enclosedElement.asType(), MODEL_CLASS)) {
				sqlType = SQL_TYPE_MAP.get(Long.class.getName());
			}
			else if (TYPE_ADAPTERS.containsKey(targetType)) {
				sqlType = SQL_TYPE_MAP.get(TYPE_ADAPTERS.get(targetType).getSerializedType());
			}
			else {
				sqlType = SQL_TYPE_MAP.get(targetType);
			}

			if (sqlType == null) {
				error(enclosedElement, "@Column type contains no SQL type mapping. (%s)", targetType);
				continue;
			}

			ColumnDefinition columnDefinition = new ColumnDefinition();
			columnDefinition.setName(name);
			columnDefinition.setTargetType(targetType);
			columnDefinition.setSqlType(sqlType);

			for (AnnotationMirror annotationMirror : enclosedElement.getAnnotationMirrors()) {
				try {
					Class annotationClass = Class.forName(annotationMirror.getAnnotationType().toString());
					columnDefinition.addAnnotation(enclosedElement.getAnnotation(annotationClass));
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

		TYPE_ADAPTERS.put(deserializedType, new TypeAdapterDefinition(classPackage, className, deserializedType, serializedType));
	}

	private void parseTableAnnotation(Element element, Map<Element, ModelAdapterDefinition> definitions) {
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
		definition.setClassPackage(classPackage);
		definition.setClassName(className + SUFFIX);
		definition.setTargetType(targetType);
		definition.setColumnDefinitions(columnDefinitions);
		definition.setTableName(tableName);

		definitions.put(element, definition);
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

	private static String getClassName(TypeElement type, String packageName) {
		int packageLen = packageName.length() + 1;
		return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
	}

	private String getPackageName(TypeElement type) {
		return elementUtils.getPackageOf(type).getQualifiedName().toString();
	}

	private void error(Element element, String message, Object... args) {
		if (args.length > 0) {
			message = String.format(message, args);
		}
		processingEnv.getMessager().printMessage(ERROR, message, element);
	}
}