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

package ollie.internal.codegen;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import ollie.internal.codegen.element.ColumnElement;
import ollie.internal.codegen.element.MigrationElement;
import ollie.internal.codegen.element.ModelAdapterElement;
import ollie.internal.codegen.element.TypeAdapterElement;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Map;
import java.util.Set;

public class Registry {
	private Messager messager;
	private Types types;
	private Elements elements;
	private Filer filer;

	private Set<MigrationElement> migrations = Sets.newHashSet();
	private Map<String, TypeAdapterElement> typeAdapters = Maps.newHashMap();
	private SetMultimap<String, ColumnElement> columns = LinkedHashMultimap.create();
	private Set<ModelAdapterElement> modelAdapters = Sets.newHashSet();

	public Registry(Messager messager, Types types, Elements elements, Filer filer) {
		this.messager = messager;
		this.types = types;
		this.elements = elements;
		this.filer = filer;
	}

	public Messager getMessager() {
		return messager;
	}

	public Types getTypes() {
		return types;
	}

	public Elements getElements() {
		return elements;
	}

	public Filer getFiler() {
		return filer;
	}

	// Migrations

	public Set<MigrationElement> getMigrationElements() {
		return migrations;
	}

	public void addMigrationElement(MigrationElement element) {
		migrations.add(element);
	}

	// Type adapters

	public TypeAdapterElement getTypeAdapterElement(TypeElement deserializedType) {
		return typeAdapters.get(deserializedType.getQualifiedName().toString());
	}

	public Set<TypeAdapterElement> getTypeAdapterElements() {
		return Sets.newHashSet(typeAdapters.values());
	}

	public void addTypeAdapterModel(TypeAdapterElement element) {
		typeAdapters.put(element.getDeserializedQualifiedName(), element);
	}

	// Columns

	public Set<ColumnElement> getColumnElements(TypeElement enclosingType) {
		return Sets.newLinkedHashSet(columns.get(enclosingType.getQualifiedName().toString()));
	}

	public void addColumnElement(ColumnElement element) {
		columns.put(element.getEnclosingQualifiedName(), element);
	}

	// Model adapters

	public Set<ModelAdapterElement> getModelAdapterElements() {
		return modelAdapters;
	}

	public void addModelAdapterElement(ModelAdapterElement element) {
		modelAdapters.add(element);
	}
}
