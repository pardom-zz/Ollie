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

package ollie.internal.codegen;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import ollie.annotation.Column;
import ollie.annotation.Migration;
import ollie.annotation.Table;
import ollie.annotation.TypeAdapter;
import ollie.internal.codegen.step.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@AutoService(Processor.class)
public class OllieProcessor extends AbstractProcessor {
	private ImmutableSet<? extends ProcessingStep> processingSteps;
	private Registry registry;

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return ImmutableSet.of(
				Migration.class.getName(),
				TypeAdapter.class.getName(),
				Table.class.getName(),
				Column.class.getName()
		);
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		registry = new Registry(
				processingEnv.getMessager(),
				processingEnv.getTypeUtils(),
				processingEnv.getElementUtils(),
				processingEnv.getFiler());

		processingSteps = ImmutableSet.of(
				new MigrationStep(registry),
				new TypeAdapterStep(registry),
				new ColumnStep(registry),
				new ModelAdapterStep(registry),
				new AdapterHolderStep(registry));
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (ProcessingStep processingStep : processingSteps) {
			processingStep.process(annotations, roundEnv);
		}
		return false;
	}
}
