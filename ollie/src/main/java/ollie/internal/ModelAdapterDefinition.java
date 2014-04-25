package ollie.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ModelAdapterDefinition {
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

	public void addColumnDefinition(ColumnDefinition columnDefinition) {
		this.columnDefinitions.add(columnDefinition);
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
		builder.append("import android.provider.BaseColumns;\n");
		builder.append("import ollie.internal.ModelAdapter;\n\n");
		builder.append("public class ").append(className).append(" extends ModelAdapter<").append(targetType).append("> {\n");
		emitGetTableName(builder);
		builder.append('\n');
		emitGetSchema(builder);
		builder.append('\n');

		builder.append("}\n");
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
		builder.append("		return \"CREATE TABLE  IF NOT EXISTS ").append(tableName).append(" (\" +\n");

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

	public static class ColumnDefinition {
		private String name;
		private String targetType;
		private String sqlType;

		private List<Annotation> annotations = new ArrayList<Annotation>();

		public void setName(String name) {
			this.name = name;
		}

		public void setTargetType(String targetType) {
			this.targetType = targetType;
		}

		public void setSqlType(String sqlType) {
			this.sqlType = sqlType;
		}

		public void setAnnotations(List<Annotation> annotations) {
			this.annotations = annotations;
		}

		public void addAnnotation(Annotation annotation) {
			this.annotations.add(annotation);
		}

		public String getSchema() {
			return name + " " + sqlType;
		}
	}
}