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

public class MigrationElement implements Comparable<MigrationElement> {
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

	@Override
	public int compareTo(MigrationElement other) {
		return getQualifiedName().compareTo(other.getQualifiedName());
	}
}
