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
