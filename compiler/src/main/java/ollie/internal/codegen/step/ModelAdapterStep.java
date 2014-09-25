package ollie.internal.codegen.step;

import ollie.annotation.Table;
import ollie.internal.codegen.Registry;
import ollie.internal.codegen.validator.Validator;
import ollie.internal.codegen.writer.SourceWriter;

import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.Set;

public class ModelAdapterStep implements ProcessingStep {
	private Validator validator;
	private SourceWriter sourceWriter;
	private Filer filer;
	private Registry registry;

	public ModelAdapterStep(Filer filer, Validator validator, SourceWriter sourceWriter, Registry registry) {
		this.validator = validator;
		this.sourceWriter = sourceWriter;
		this.filer = filer;
		this.registry = registry;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Table.class);
		for (Element element : elements) {
			if (validator.validate(element)) {
				registry.addModelAdapterElement((TypeElement) element);

				try {
					JavaFileObject object = filer.createSourceFile(sourceWriter.createSourceName(element), element);
					Writer writer = object.openWriter();
					sourceWriter.writeSource(writer, element);
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}
