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

import ollie.internal.codegen.Registry;
import ollie.internal.codegen.writer.AdapterHolderWriter;
import ollie.internal.codegen.writer.SourceWriter;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

public class AdapterHolderStep implements ProcessingStep {
	private Registry registry;
	private SourceWriter sourceWriter;

	public AdapterHolderStep(Registry registry) {
		this.registry = registry;
		this.sourceWriter = new AdapterHolderWriter(registry);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			try {
				String name = sourceWriter.createSourceName(null);
				JavaFileObject object = registry.getFiler().createSourceFile(name);
				Writer writer = object.openWriter();
				sourceWriter.writeSource(writer, null);
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return false;
	}
}
