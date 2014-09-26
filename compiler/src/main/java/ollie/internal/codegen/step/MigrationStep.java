package ollie.internal.codegen.step;

import ollie.annotation.Migration;
import ollie.internal.codegen.Registry;
import ollie.internal.codegen.validator.MigrationValidator;
import ollie.internal.codegen.validator.Validator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public class MigrationStep implements ProcessingStep {
	private Registry registry;
	private Validator validator;

	public MigrationStep(Registry registry) {
		this.registry = registry;
		this.validator = new MigrationValidator();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Migration.class);
		for (Element element : elements) {
			if (validator.validate(element.getEnclosingElement(), element)) {
				registry.addMigrationElement((TypeElement) element);
			}
		}
		return false;
	}
}
