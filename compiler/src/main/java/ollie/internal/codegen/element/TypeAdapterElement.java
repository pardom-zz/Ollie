package ollie.internal.codegen.element;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.List;

public class TypeAdapterElement {
	private TypeElement element;
	private TypeElement deserializedTypeElement;
	private TypeElement serializedTypeElement;

	public TypeAdapterElement(Types typeUtils, Elements elementUtils, TypeElement element) {
		this.element = element;

		DeclaredType typeAdapterInterface = null;
		final DeclaredType typeAdapterType = typeUtils.getDeclaredType(
				elementUtils.getTypeElement("ollie.TypeAdapter"),
				typeUtils.getWildcardType(null, null),
				typeUtils.getWildcardType(null, null)
		);


		for (TypeMirror superType : typeUtils.directSupertypes(element.asType())) {
			if (typeUtils.isAssignable(superType, typeAdapterType)) {
				typeAdapterInterface = (DeclaredType) superType;
				break;
			}
		}

		if (typeAdapterInterface != null) {
			final List<? extends TypeMirror> typeArguments = typeAdapterInterface.getTypeArguments();
			deserializedTypeElement = elementUtils.getTypeElement(typeArguments.get(0).toString());
			serializedTypeElement = elementUtils.getTypeElement(typeArguments.get(1).toString());
		}
	}

	public String getQualifiedName() {
		return element.getQualifiedName().toString();
	}

	public String getSimpleName() {
		return element.getSimpleName().toString();
	}

	public TypeElement getDeserializedType() {
		return deserializedTypeElement;
	}

	public String getDeserializedQualifiedName() {
		return deserializedTypeElement.getQualifiedName().toString();
	}

	public String getDeserializedSimpleName() {
		return deserializedTypeElement.getSimpleName().toString();
	}

	public TypeElement getSerializedType() {
		return serializedTypeElement;
	}

	public String getSerializedQualifiedName() {
		return serializedTypeElement.getQualifiedName().toString();
	}

	public String getSerializedSimpleName() {
		return serializedTypeElement.getSimpleName().toString();
	}
}
