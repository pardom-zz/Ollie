package ollie.internal.codegen.step;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public interface ProcessingStep {
	boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv);
}
