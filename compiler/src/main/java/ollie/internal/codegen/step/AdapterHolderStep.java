package ollie.internal.codegen.step;

import ollie.internal.codegen.writer.SourceWriter;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

public class AdapterHolderStep implements ProcessingStep {
	private Filer filer;
	private SourceWriter sourceWriter;

	public AdapterHolderStep(Filer filer, SourceWriter sourceWriter) {
		this.filer = filer;
		this.sourceWriter = sourceWriter;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			try {
				JavaFileObject object = filer.createSourceFile(sourceWriter.createSourceName(null));
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
