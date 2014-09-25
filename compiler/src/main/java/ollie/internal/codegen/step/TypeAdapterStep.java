package ollie.internal.codegen.step;

import ollie.adapter.BooleanAdapter;
import ollie.adapter.CalendarAdapter;
import ollie.adapter.SqlDateAdapter;
import ollie.adapter.UtilDateAdapter;
import ollie.annotation.TypeAdapter;
import ollie.internal.codegen.Registry;
import ollie.internal.codegen.validator.Validator;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.HashSet;
import java.util.Set;

public class TypeAdapterStep implements ProcessingStep {
	private static final Class[] DEFAULT_TYPE_ADAPTERS = new Class[]{
			BooleanAdapter.class,
			CalendarAdapter.class,
			SqlDateAdapter.class,
			UtilDateAdapter.class
	};

	private Elements elementUtils;
	private Validator validator;
	private Registry registry;

	public TypeAdapterStep(Elements elements, Validator validator, Registry registry) {
		this.elementUtils = elements;
		this.validator = validator;
		this.registry = registry;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		final Set<Element> elements = new HashSet<Element>(roundEnv.getElementsAnnotatedWith(TypeAdapter.class));
		for (Class cls : DEFAULT_TYPE_ADAPTERS) {
			elements.add(elementUtils.getTypeElement(cls.getName()));
		}

		for (Element element : elements) {
			if (validator.validate(element)) {
				registry.addTypeAdapterModel((TypeElement) element);
			}
		}

		return false;
	}
}
