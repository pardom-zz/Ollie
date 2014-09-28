/*
 * Copyright (C) 2014 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

	public TypeAdapterElement(Types types, Elements elements, TypeElement element) {
		this.element = element;

		DeclaredType typeAdapterInterface = null;
		final DeclaredType typeAdapterType = types.getDeclaredType(
				elements.getTypeElement("ollie.TypeAdapter"),
				types.getWildcardType(null, null),
				types.getWildcardType(null, null)
		);


		for (TypeMirror superType : types.directSupertypes(element.asType())) {
			if (types.isAssignable(superType, typeAdapterType)) {
				typeAdapterInterface = (DeclaredType) superType;
				break;
			}
		}

		if (typeAdapterInterface != null) {
			final List<? extends TypeMirror> typeArguments = typeAdapterInterface.getTypeArguments();
			deserializedTypeElement = elements.getTypeElement(typeArguments.get(0).toString());
			serializedTypeElement = elements.getTypeElement(typeArguments.get(1).toString());
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
