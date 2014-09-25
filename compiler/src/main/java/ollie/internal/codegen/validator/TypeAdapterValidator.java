package ollie.internal.codegen.validator;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.tools.Diagnostic.Kind.ERROR;

public class TypeAdapterValidator implements Validator {
	private Messager messager;

	public TypeAdapterValidator(Messager messager) {
		this.messager = messager;
	}

	@Override
	public boolean validate(Element element) {
		if (!element.getKind().equals(CLASS)) {
			messager.printMessage(ERROR, "@TypeAdapter applies only to Model classes.", element);
			return false;
		}

		return true;
	}
}
