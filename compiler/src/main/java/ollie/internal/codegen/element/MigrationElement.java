package ollie.internal.codegen.element;

import javax.lang.model.element.TypeElement;

public class MigrationElement {
	private TypeElement element;

	public MigrationElement(TypeElement element) {
		this.element = element;
	}

	public String getQualifiedName() {
		return element.getQualifiedName().toString();
	}

	public String getSimpleName() {
		return element.getSimpleName().toString();
	}
}
