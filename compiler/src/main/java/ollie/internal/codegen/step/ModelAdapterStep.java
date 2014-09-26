package ollie.internal.codegen.step;

import com.google.common.collect.Sets;
import ollie.annotation.Column;
import ollie.annotation.Table;
import ollie.internal.codegen.Registry;
import ollie.internal.codegen.element.ColumnElement;
import ollie.internal.codegen.validator.ColumnValidator;
import ollie.internal.codegen.validator.ModelAdapterValidator;
import ollie.internal.codegen.writer.ModelAdapterWriter;
import ollie.internal.codegen.writer.SourceWriter;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

public class ModelAdapterStep implements ProcessingStep {
	private Registry registry;
	private Elements elements;
	private Filer filer;
	private ModelAdapterValidator validator;
	private ColumnValidator columnValidator;
	private SourceWriter sourceWriter;

	public ModelAdapterStep(Registry registry) {
		this.registry = registry;
		this.elements = registry.getElements();
		this.filer = registry.getFiler();
		this.validator = new ModelAdapterValidator(registry);
		this.columnValidator = new ColumnValidator(registry);
		this.sourceWriter = new ModelAdapterWriter(registry);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Set<? extends Element> tableElements = roundEnv.getElementsAnnotatedWith(Table.class);
		for (Element tableElement : tableElements) {
			if (validator.validate(tableElement.getEnclosingElement(), tableElement)) {
				registry.addModelAdapterElement((TypeElement) tableElement);

				Set<ColumnElement> columnElements = getColumnElements((TypeElement) tableElement);
				for (ColumnElement columnElement : columnElements) {
					registry.addColumnElements(columnElement);
				}

				try {
					String name = sourceWriter.createSourceName(tableElement);
					JavaFileObject object = filer.createSourceFile(name, tableElement);
					Writer writer = object.openWriter();
					sourceWriter.writeSource(writer, tableElement);
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	private Set<ColumnElement> getColumnElements(TypeElement element) {
		final Set<ColumnElement> columnElements = Sets.newLinkedHashSet();
		final List<? extends Element> members = elements.getAllMembers(element);

		boolean isColumn;
		for (Element member : members) {
			isColumn = (member.getAnnotation(Column.class) != null);
			if (isColumn && columnValidator.validate(element, member)) {
				columnElements.add(new ColumnElement(registry, element, (VariableElement) member));
			}
		}

		return columnElements;
	}
}
