package ollie.internal;

import java.lang.annotation.Annotation;
import java.util.*;

public class ModelAdapterDefinition {
	private static final Map<String, String> CURSOR_METHOD_MAP = new HashMap<String, String>() {
		{
			put(byte[].class.getName(), "getBlob");
			put(Byte[].class.getName(), "getBlob");
			put(double.class.getName(), "getDouble");
			put(Double.class.getName(), "getDouble");
			put(float.class.getName(), "getFloat");
			put(Float.class.getName(), "getFloat");
			put(int.class.getName(), "getInt");
			put(Integer.class.getName(), "getInt");
			put(long.class.getName(), "getLong");
			put(Long.class.getName(), "getLong");
			put(short.class.getName(), "getShort");
			put(Short.class.getName(), "getShort");
			put(String.class.getName(), "getString");
		}
	};

	private String classPackage;
	private String className;
	private String targetType;

	private String tableName;
	private Set<ColumnDefinition> columnDefinitions = new LinkedHashSet<ColumnDefinition>();

	public void setClassPackage(String classPackage) {
		this.classPackage = classPackage;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setColumnDefinitions(Set<ColumnDefinition> columnDefinitions) {
		this.columnDefinitions = columnDefinitions;
	}

	public String getFqcn() {
		return classPackage + "." + className;
	}

	public String brewJava() {
		StringBuilder builder = new StringBuilder();
		builder.append("// Generated code from Ollie. Do not modify!\n");
		builder.append("package ollie;\n\n");
		builder.append("import android.content.ContentValues;\n");
		builder.append("import android.database.Cursor;\n");
		builder.append("import android.database.sqlite.SQLiteDatabase;\n");
		builder.append("import ollie.internal.ModelAdapter;\n\n");
		builder.append("public class ").append(className).append(" extends ModelAdapter<").append(targetType).append("> {\n");
		emitGetTableName(builder);
		builder.append('\n');
		emitGetSchema(builder);
		builder.append('\n');
		emitLoad(builder);
		builder.append('\n');
		emitSave(builder);
		builder.append('\n');
		builder.append("}");
		return builder.toString();
	}

	private void emitGetTableName(StringBuilder builder) {
		builder.append("	@Override\n");
		builder.append("	public String getTableName() {\n");
		builder.append("		return \"").append(tableName).append("\";\n");
		builder.append("	}\n");
	}

	private void emitGetSchema(StringBuilder builder) {
		builder.append("	@Override\n");
		builder.append("	public String getSchema() {\n");
		builder.append("		return \"CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (\" +\n");

		int current = 0;
		int count = columnDefinitions.size();
		for (ColumnDefinition columnDefinition : columnDefinitions) {
			builder.append("			\"").append(columnDefinition.getSchema());
			if (++current < count) {
				builder.append(", ");
			}
			builder.append("\" +\n");
		}

		builder.append("			\")\";\n");
		builder.append("	}\n");
	}

	private void emitLoad(StringBuilder builder) {
		builder.append("	@Override\n");
		builder.append("	public void load(").append(targetType).append(" entity, Cursor cursor) {\n");

		for (ColumnDefinition columnDefinition : columnDefinitions) {
			builder.append("		entity.").append(columnDefinition.targetName).append(" = ");

			int closeParens = 0;
			if (columnDefinition.isModel) {
				closeParens++;
				builder.append("Ollie.getOrFindEntity(entity.").append(columnDefinition.targetName).append(".getClass(), ");
			}
			else if (columnDefinition.requiresTypeAdapter()) {
				closeParens++;
				builder.append("Ollie.getTypeAdapter(").append(columnDefinition.deserializedType).append(".class).deserialize(");
			}

			builder.append("cursor.").append(CURSOR_METHOD_MAP.get(columnDefinition.serializedType)).append("(");
			builder.append("cursor.getColumnIndex(\"").append(columnDefinition.name).append("\")");

			for (int i = 0; i < closeParens; i++) {
				builder.append(")");
			}

			builder.append(");\n");
		}

		builder.append("	}\n");
	}

	private void emitSave(StringBuilder builder) {
		builder.append("	@Override\n");
		builder.append("	public Long save(").append(targetType).append(" entity, SQLiteDatabase db) {\n");
		builder.append("		ContentValues values = new ContentValues();\n");

		for (ColumnDefinition columnDefinition : columnDefinitions) {
			builder.append("		values.put(\"").append(columnDefinition.name).append("\", ");

			int closeParens = 0;
			if (columnDefinition.requiresTypeAdapter()) {
				closeParens++;
				builder.append("(").append(columnDefinition.serializedType).append(") Ollie.getTypeAdapter(").append(columnDefinition.deserializedType).append(".class).serialize(");
			}

			builder.append("entity.").append(columnDefinition.targetName);

			if (columnDefinition.isModel) {
				builder.append(".id");
			}

			for (int i = 0; i < closeParens; i++) {
				builder.append(")");
			}

			builder.append(");\n");
		}

		builder.append("		return insertOrUpdate(entity, db, values);\n");
		builder.append("	}");
	}

	public static class ColumnDefinition {
		private String name;
		private String targetName;
		private String deserializedType;
		private String serializedType;
		private String sqlType;
		private boolean isModel;

		private List<Annotation> annotations = new ArrayList<Annotation>();

		public void setName(String name) {
			this.name = name;
		}

		public void setTargetName(String targetName) {
			this.targetName = targetName;
		}

		public void setDeserializedType(String deserializedType) {
			this.deserializedType = deserializedType;
		}

		public void setSerializedType(String serializedType) {
			this.serializedType = serializedType;
		}

		public void setSqlType(String sqlType) {
			this.sqlType = sqlType;
		}

		public void setIsModel(boolean isModelSubclass) {
			this.isModel = isModelSubclass;
		}

		public void addAnnotation(Annotation annotation) {
			this.annotations.add(annotation);
		}

		public boolean requiresTypeAdapter() {
			return !deserializedType.equals(serializedType) && !isModel;
		}

		public String getSchema() {
			return name + " " + sqlType;
		}
	}
}