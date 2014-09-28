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

package ollie.internal.codegen.step;

import ollie.annotation.Migration;
import ollie.internal.codegen.Registry;
import ollie.internal.codegen.element.MigrationElement;
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
				registry.addMigrationElement(new MigrationElement((TypeElement) element));
			}
		}
		return false;
	}
}
