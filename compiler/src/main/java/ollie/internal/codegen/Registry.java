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

	public void addMigrationElement(TypeElement element) {
		migrations.add(new MigrationElement(element));
	}

	// Type adapters

	public TypeAdapterElement getTypeAdapterElement(TypeElement deserializedType) {
		return typeAdapters.get(deserializedType.getQualifiedName().toString());
	}

	public Set<TypeAdapterElement> getTypeAdapterElements() {
		return Sets.newHashSet(typeAdapters.values());
	}

	public void addTypeAdapterModel(TypeElement element) {
		TypeAdapterElement model = new TypeAdapterElement(types, elements, element);
		typeAdapters.put(model.getDeserializedQualifiedName(), model);
	}

	// Columns

	public Set<ColumnElement> getColumnElements(TypeElement enclosingType) {
		return Sets.newLinkedHashSet(columns.get(enclosingType.getQualifiedName().toString()));
	}

	public void addColumnElements(ColumnElement element) {
		columns.put(element.getEnclosingQualifiedName(), element);
	}

	// Model adapters

	public Set<ModelAdapterElement> getModelAdapterElements() {
		return modelAdapters;
	}

	public void addModelAdapterElement(TypeElement element) {
		modelAdapters.add(new ModelAdapterElement(element));
	}
}
