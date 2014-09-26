package ollie.internal.codegen.validator;

import ollie.annotation.Column;
import ollie.annotation.Table;
import ollie.internal.codegen.Errors;
import ollie.internal.codegen.Registry;
import ollie.internal.codegen.element.ColumnElement;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.FIELD;
import static javax.tools.Diagnostic.Kind.ERROR;

public class ColumnValidator implements Validator {
	private Registry registry;
	private Messager messager;

	public ColumnValidator(Registry registry) {
		this.registry = registry;
		this.messager = registry.getMessager();
	}

	@Override
	public boolean validate(Element enclosingElement, Element element) {
		if (!element.getKind().equals(FIELD)) {
			messager.printMessage(ERROR, Errors.COLUMN_TYPE_ERROR, element);
			return false;
		}

		Table table = enclosingElement.getAnnotation(Table.class);
		if (!enclosingElement.getKind().equals(CLASS) || table == null) {
			messager.printMessage(ERROR, "@Column fields can only be enclosed by model classes.", element);
			return false;
		}

		Column column = element.getAnnotation(Column.class);
		Set<ColumnElement> existingColumns = registry.getColumnElements((TypeElement) enclosingElement);
		for (ColumnElement existingColumn : existingColumns) {
			if (existingColumn.getColumnName().equals(column.value())) {
				messager.printMessage(ERROR, Errors.COLUMN_DUPLICATE_ERROR + column.value(), element);
				return false;
			}
		}

		return true;
	}
}
