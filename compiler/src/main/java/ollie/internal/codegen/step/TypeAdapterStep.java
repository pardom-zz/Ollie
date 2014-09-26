package ollie.internal.codegen.step;

import com.google.common.collect.Sets;
import ollie.adapter.BooleanAdapter;
import ollie.adapter.CalendarAdapter;
import ollie.adapter.SqlDateAdapter;
import ollie.adapter.UtilDateAdapter;
import ollie.annotation.TypeAdapter;
import ollie.internal.codegen.Registry;
import ollie.internal.codegen.element.TypeAdapterElement;
import ollie.internal.codegen.validator.TypeAdapterValidator;
import ollie.internal.codegen.validator.Validator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public class TypeAdapterStep implements ProcessingStep {
	private static final Class[] DEFAULT_TYPE_ADAPTERS = new Class[]{
			BooleanAdapter.class,
			CalendarAdapter.class,
			SqlDateAdapter.class,
			UtilDateAdapter.class
	};

	private Registry registry;
	private Validator validator;

	public TypeAdapterStep(Registry registry) {
		this.registry = registry;
		this.validator = new TypeAdapterValidator(registry);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		final Set<Element> elements = Sets.newHashSet(roundEnv.getElementsAnnotatedWith(TypeAdapter.class));
		for (Class cls : DEFAULT_TYPE_ADAPTERS) {
			elements.add(registry.getElements().getTypeElement(cls.getName()));
		}

		for (Element element : elements) {
			if (validator.validate(element.getEnclosingElement(), element)) {
				registry.addTypeAdapterModel(new TypeAdapterElement(
						registry.getTypes(),
						registry.getElements(),
						(TypeElement) element));
			}
		}

		return false;
	}
}
