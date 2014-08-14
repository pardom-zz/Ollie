package ollie.internal;

import ollie.Model;
import ollie.annotation.*;
import ollie.annotation.ForeignKey.Deferrable;
import ollie.annotation.ForeignKey.DeferrableTiming;
import ollie.annotation.ForeignKey.ReferentialAction;

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

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public String getTableName() {
		return tableName;
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
		emitGetModelType(builder);
		builder.append('\n');
		emitGetTableName(builder);
		builder.append('\n');
		emitGetSchema(builder);
		builder.append('\n');
		emitLoad(builder);
		builder.append('\n');
		emitSave(builder);
		builder.append('\n');
		emitDelete(builder);
		builder.append("}");
		return builder.toString();
	}

	private void emitGetModelType(StringBuilder builder) {
		builder.append("	@Override\n");
		builder.append("	public Class<? extends Model> getModelType() {\n");
		builder.append("		return ").append(targetType).append(".class;\n");
		builder.append("	}\n");
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
		builder.append("		return \"CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (");

		List<String> definitions = new ArrayList<String>();
		for (ColumnDefinition columnDefinition : columnDefinitions) {
			definitions.add(columnDefinition.getSchema());
		}
		for (ColumnDefinition columnDefinition : columnDefinitions) {
			String foreignKeyClause = columnDefinition.getForeignKeyClause();
			if (!TextUtils.isEmpty(foreignKeyClause)) {
				definitions.add(columnDefinition.getForeignKeyClause());
			}
		}

		builder.append(TextUtils.join(", ", definitions));

		builder.append(")\";\n");
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
			} else if (columnDefinition.requiresTypeAdapter()) {
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
				builder.append(" != null ? ");
				builder.append("entity.").append(columnDefinition.targetName).append(".id");
				builder.append(" : null");
			}

			for (int i = 0; i < closeParens; i++) {
				builder.append(")");
			}

			builder.append(");\n");
		}

		builder.append("		return insertOrUpdate(entity, db, values);\n");
		builder.append("	}\n");
	}

	private void emitDelete(StringBuilder builder) {
		builder.append("	@Override\n");
		builder.append("	public void delete(").append(targetType).append(" entity, SQLiteDatabase db) {\n");
		builder.append("		db.delete(\"").append(tableName).append("\", \"").append(Model._ID).append("=?\", new String[]{entity.id.toString()});\n");
		builder.append("	}\n");
	}

	public static class ColumnDefinition {
		private String name;
		private String targetName;
		private String deserializedType;
		private String serializedType;
		private String sqlType;

		private boolean isModel;
		private String modelTableName;

		private Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<Class<? extends Annotation>, Annotation>();

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

		public void setModelTableName(String modelTableName) {
			this.modelTableName = modelTableName;
		}

		public void putAnnotation(Class<? extends Annotation> cls, Annotation annotation) {
			this.annotations.put(cls, annotation);
		}

		public boolean requiresTypeAdapter() {
			return !deserializedType.equals(serializedType) && !isModel;
		}

		public String getSchema() {
			StringBuilder builder = new StringBuilder();
			builder.append(name);
			builder.append(" ");
			builder.append(sqlType);

			if (annotations.containsKey(PrimaryKey.class)) {
				builder.append(" PRIMARY KEY");
			}
			if (annotations.containsKey(AutoIncrement.class)) {
				builder.append(" AUTOINCREMENT");
			}
			if (annotations.containsKey(NotNull.class)) {
				builder.append(" NOT NULL");
				appendConflictClause(builder, ((NotNull) annotations.get(NotNull.class)).value());
			}
			if (annotations.containsKey(Unique.class)) {
				builder.append(" UNIQUE");
				appendConflictClause(builder, ((Unique) annotations.get(Unique.class)).value());
			}
			if (annotations.containsKey(Check.class)) {
				builder.append(" CHECK (").append(((Check) annotations.get(Check.class)).value()).append(")");
			}
			if (annotations.containsKey(Default.class)) {
				builder.append(" DEFAULT ").append(((Default) annotations.get(Default.class)).value());
			}
			if (annotations.containsKey(Collate.class)) {
				builder.append(" COLLATE ").append(((Collate) annotations.get(Collate.class)).value().keyword());
			}

			return builder.toString();
		}

		public String getForeignKeyClause() {
			StringBuilder builder = new StringBuilder();

			if (isModel && annotations.containsKey(ForeignKey.class)) {
				ForeignKey foreignKey = (ForeignKey) annotations.get(ForeignKey.class);

				builder.append("FOREIGN KEY(").append(name).append(") REFERENCES ");
				builder.append(modelTableName);

				if (foreignKey.foreignColumns().length > 0) {
					builder.append("(");
					builder.append(TextUtils.join(",", foreignKey.foreignColumns()));
					builder.append(")");
				}
				if (!foreignKey.onDelete().equals(ReferentialAction.NONE)) {
					builder.append(" ON DELETE ").append(foreignKey.onDelete().keyword());
				}
				if (!foreignKey.onUpdate().equals(ReferentialAction.NONE)) {
					builder.append(" ON UPDATE ").append(foreignKey.onUpdate().keyword());
				}
				if (!TextUtils.isEmpty(foreignKey.match())) {
					builder.append(" MATCH ").append(foreignKey.match());
				}
				if (!foreignKey.deferrable().equals(Deferrable.NONE)) {
					builder.append(" ").append(foreignKey.deferrable().keyword());

					if (!foreignKey.deferrableTiming().equals(DeferrableTiming.NONE)) {
						builder.append(" ").append(foreignKey.deferrableTiming().keyword());
					}
				}
			}

			return builder.toString();
		}

		private void appendConflictClause(StringBuilder builder, ConflictClause conflictClause) {
			if (!conflictClause.equals(ConflictClause.NONE)) {
				builder.append(" ON CONFLICT ").append(conflictClause.keyword());
			}
		}
	}
}