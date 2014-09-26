package ollie.internal.codegen.validator;

import javax.lang.model.element.Element;

public interface Validator {
	boolean validate(Element enclosingElement, Element element);
}
