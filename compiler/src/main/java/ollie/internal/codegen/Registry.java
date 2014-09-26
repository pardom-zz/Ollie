package ollie.internal.codegen;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import ollie.internal.codegen.element.ColumnElement;
import ollie.internal.codegen.element.MigrationElement;
import ollie.internal.codegen.element.ModelAdapterElement;
import ollie.internal.codegen.element.TypeAdapterElement;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Map;
import java.util.Set;

public class Registry {
	private Types types;
	private Elements elements;

	private Set<MigrationElement> migrationElements = Sets.newHashSet();
	private Map<String, TypeAdapterElement> typeAdapters = Maps.newHashMap();
	private Multimap<String, ColumnElement> columnModels = HashMultimap.create();
	private Set<ModelAdapterElement> modelAdapterModels = Sets.newHashSet();

	public Registry(Types types, Elements elements) {
		this.types = types;
		this.elements = elements;
	}

	// Migrations

	public Set<MigrationElement> getMigrationElements() {
		return migrationElements;
	}

	public void addMigrationElement(TypeElement element) {
		migrationElements.add(new MigrationElement(element));
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
		return Sets.newHashSet(columnModels.get(enclosingType.getQualifiedName().toString()));
	}

	public void addColumnElement(VariableElement element) {
		ColumnElement columnElement = new ColumnElement(types, elements, this, element);
		columnModels.put(columnElement.getEnclosingQualifiedName(), columnElement);
	}

	// Model adapters

	public Set<ModelAdapterElement> getModelAdapterElements() {
		return modelAdapterModels;
	}

	public void addModelAdapterElement(TypeElement element) {
		modelAdapterModels.add(new ModelAdapterElement(element));
	}
}
