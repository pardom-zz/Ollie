package ollie.internal.codegen.validator;

import javax.lang.model.element.Element;

public class MigrationValidator implements Validator {
	@Override
	public boolean validate(Element enclosingElement, Element element) {
		return false;
	}
}
