package ollie.internal.codegen.validator;

import ollie.internal.codegen.Registry;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.tools.Diagnostic.Kind.ERROR;

public class ModelAdapterValidator implements Validator {
	private Messager messager;
	private Registry registry;

	public ModelAdapterValidator(Messager messager, Registry registry) {
		this.messager = messager;
		this.registry = registry;
	}

	@Override
	public boolean validate(Element element) {
		if (!element.getKind().equals(CLASS)) {
			messager.printMessage(ERROR, "@Table applies only to Model classes.", element);
			return false;
		}

		return true;
	}
}
