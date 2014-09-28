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

import ollie.annotation.Column;
import ollie.annotation.Table;
import ollie.internal.codegen.Registry;
import ollie.internal.codegen.element.ColumnElement;
import ollie.internal.codegen.element.ModelAdapterElement;
import ollie.internal.codegen.validator.ColumnValidator;
import ollie.internal.codegen.validator.ModelAdapterValidator;
import ollie.internal.codegen.writer.ModelAdapterWriter;
import ollie.internal.codegen.writer.SourceWriter;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Set;

public class ModelAdapterStep implements ProcessingStep {
	private Registry registry;
	private Elements elements;
	private Filer filer;
	private ModelAdapterValidator validator;
	private ColumnValidator columnValidator;
	private SourceWriter sourceWriter;

	public ModelAdapterStep(Registry registry) {
		this.registry = registry;
		this.elements = registry.getElements();
		this.filer = registry.getFiler();
		this.validator = new ModelAdapterValidator(registry);
		this.columnValidator = new ColumnValidator(registry);
		this.sourceWriter = new ModelAdapterWriter(registry);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Set<? extends Element> tableElements = roundEnv.getElementsAnnotatedWith(Table.class);
		for (Element tableElement : tableElements) {
			if (validator.validate(tableElement.getEnclosingElement(), tableElement)) {
				registry.addModelAdapterElement(new ModelAdapterElement((TypeElement) tableElement));

				addColumnElements((TypeElement) tableElement);

				try {
					String name = sourceWriter.createSourceName(tableElement);
					JavaFileObject object = filer.createSourceFile(name, tableElement);
					Writer writer = object.openWriter();
					sourceWriter.writeSource(writer, tableElement);
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	private void addColumnElements(TypeElement element) {
		final List<? extends Element> members = elements.getAllMembers(element);
		boolean isColumn;

		for (Element member : members) {
			isColumn = (member.getAnnotation(Column.class) != null);
			if (isColumn && columnValidator.validate(element, member)) {
				registry.addColumnElement(new ColumnElement(registry, element, (VariableElement) member));
			}
		}
	}
}
