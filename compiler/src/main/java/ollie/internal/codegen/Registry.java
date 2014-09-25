package ollie.internal.codegen;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
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
import java.util.*;

public class Registry {
	private Types types;
	private Elements elements;

	private Set<MigrationElement> migrationElements = new HashSet<MigrationElement>();
	private Map<String, TypeAdapterElement> typeAdapters = new HashMap<String, TypeAdapterElement>();
	private Multimap<String, ColumnElement> columnModels = LinkedHashMultimap.create();
	private Set<ModelAdapterElement> modelAdapterModels = new HashSet<ModelAdapterElement>();

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

	public Set<TypeAdapterElement> getTypeAdapters() {
		return Sets.newHashSet(typeAdapters.values());
	}

	public void addTypeAdapterModel(TypeElement element) {
		TypeAdapterElement model = new TypeAdapterElement(types, elements, element);
		typeAdapters.put(model.getDeserializedQualifiedName(), model);
	}

	// Columns

	public List<ColumnElement> getColumnElements(TypeElement enclosingType) {
		return Lists.newArrayList(columnModels.get(enclosingType.getQualifiedName().toString()));
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
