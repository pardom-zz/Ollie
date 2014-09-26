package ollie.internal.codegen.step;

import com.google.common.collect.Sets;
import ollie.annotation.Column;
import ollie.internal.codegen.Registry;
import ollie.internal.codegen.validator.ColumnValidator;
import ollie.internal.codegen.validator.Validator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public class ColumnStep implements ProcessingStep {
	private Validator validator;

	public ColumnStep(Registry registry) {
		this.validator = new ColumnValidator(registry);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		final Set<Element> elements = Sets.newHashSet(roundEnv.getElementsAnnotatedWith(Column.class));
		for (Element element : elements) {
			validator.validate(element.getEnclosingElement(), element);
		}
		return false;
	}
}
