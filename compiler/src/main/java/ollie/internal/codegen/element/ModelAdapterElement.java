package ollie.internal.codegen.element;

import javax.lang.model.element.TypeElement;

public class ModelAdapterElement {
	private TypeElement element;

	public ModelAdapterElement(TypeElement element) {
		this.element = element;
	}

	public String getQualifiedName() {
		return "ollie." + element.getSimpleName() + "$$ModelAdapter";
	}

	public String getModelQualifiedName() {
		return element.getQualifiedName().toString();
	}
}
