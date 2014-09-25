package ollie.internal.codegen;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import ollie.annotation.Column;
import ollie.annotation.Migration;
import ollie.annotation.Table;
import ollie.annotation.TypeAdapter;
import ollie.internal.codegen.step.*;
import ollie.internal.codegen.validator.ColumnValidator;
import ollie.internal.codegen.validator.MigrationValidator;
import ollie.internal.codegen.validator.ModelAdapterValidator;
import ollie.internal.codegen.validator.TypeAdapterValidator;
import ollie.internal.codegen.writer.AdapterHolderWriter;
import ollie.internal.codegen.writer.ModelAdapterWriter;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
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
				Column.class.getName(),
				Table.class.getName()
		);
	}

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);

		final Messager messager = processingEnv.getMessager();
		final Types types = processingEnv.getTypeUtils();
		final Elements elements = processingEnv.getElementUtils();
		final Filer filer = processingEnv.getFiler();

		registry = new Registry(types, elements);

		processingSteps = ImmutableSet.of(
				new MigrationStep(
						new MigrationValidator(),
						registry),
				new TypeAdapterStep(
						elements,
						new TypeAdapterValidator(messager),
						registry),
				new ColumnStep(
						new ColumnValidator(messager, registry),
						registry),
				new ModelAdapterStep(
						filer,
						new ModelAdapterValidator(messager, registry),
						new ModelAdapterWriter(registry),
						registry),
				new AdapterHolderStep(
						filer,
						new AdapterHolderWriter(registry)
				)
		);

	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (ProcessingStep processingStep : processingSteps) {
			processingStep.process(annotations, roundEnv);
		}
		return false;
	}
}
