/*
 * Copyright (C) 2014 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ollie.internal.codegen.element;

import android.text.TextUtils;
import com.google.common.collect.Maps;
import ollie.annotation.*;
import ollie.internal.codegen.Registry;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ollie.annotation.ForeignKey.DeferrableTiming;
import static ollie.annotation.ForeignKey.ReferentialAction;

public class ColumnElement {
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

	private Column column;
	private VariableElement element;
	private TypeElement enclosingType;
	private TypeElement deserializedType;
	private TypeElement serializedType;
	private String sqlType;

	private boolean isModel;
	private String modelTableName;

	private Map<Class<? extends Annotation>, Annotation> annotations = Maps.newHashMap();

	public ColumnElement(Registry registry, TypeElement enclosingType, VariableElement element) {
		this.element = element;
		this.column = element.getAnnotation(Column.class);
		this.enclosingType = enclosingType;
		this.deserializedType = registry.getElements().getTypeElement(element.asType().toString());

		final TypeAdapterElement typeAdapterElement = registry.getTypeAdapterElement(deserializedType);
		final TypeElement modelElement = registry.getElements().getTypeElement("ollie.Model");
		final DeclaredType modelType = registry.getTypes().getDeclaredType(modelElement);
		isModel = registry.getTypes().isAssignable(element.asType(), modelType);

		if (isModel) {
			final Table table = deserializedType.getAnnotation(Table.class);
			serializedType = registry.getElements().getTypeElement(Long.class.getName());
			modelTableName = table.value();
		} else if (typeAdapterElement != null) {
			serializedType = typeAdapterElement.getSerializedType();
		} else {
			serializedType = deserializedType;
		}

		this.sqlType = SQL_TYPE_MAP.get(getSerializedQualifiedName());

		List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
		for (AnnotationMirror annotationMirror : annotationMirrors) {
			try {
				Class annotationClass = Class.forName(annotationMirror.getAnnotationType().toString());
				annotations.put(annotationClass, element.getAnnotation(annotationClass));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean isModel() {
		return isModel;
	}

	public String getFieldName() {
		return element.getSimpleName().toString();
	}

	public String getColumnName() {
		return column.value();
	}

	public TypeElement getEnclosingElement() {
		return enclosingType;
	}

	public String getEnclosingQualifiedName() {
		return enclosingType.getQualifiedName().toString();
	}

	public String getDeserializedQualifiedName() {
		return deserializedType.getQualifiedName().toString();
	}

	public String getDeserializedSimpleName() {
		return deserializedType.getSimpleName().toString();
	}

	public String getSerializedQualifiedName() {
		return serializedType.getQualifiedName().toString();
	}

	public String getSerializedSimpleName() {
		return serializedType.getSimpleName().toString();
	}

	public boolean requiresTypeAdapter() {
		return !serializedType.getQualifiedName().equals(deserializedType.getQualifiedName());
	}

	public String getSchema() {
		StringBuilder builder = new StringBuilder();
		builder.append(getColumnName());
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

			builder.append("FOREIGN KEY(").append(getColumnName()).append(") REFERENCES ");
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
			if (!foreignKey.deferrable().equals(ForeignKey.Deferrable.NONE)) {
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
