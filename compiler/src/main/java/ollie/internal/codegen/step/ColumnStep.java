package ollie.internal.codegen.step;

import ollie.annotation.Column;
import ollie.internal.codegen.Registry;
import ollie.internal.codegen.validator.Validator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import java.util.Set;

public class ColumnStep implements ProcessingStep {
	private Validator validator;
	private Registry registry;

	public ColumnStep(Validator validator, Registry registry) {
		this.validator = validator;
		this.registry = registry;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Column.class);
		for (Element element : elements) {
			if (validator.validate(element)) {
				registry.addColumnElement((VariableElement) element);
			}
		}
		return false;
	}
}
