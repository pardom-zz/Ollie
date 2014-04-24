package ollie.internal;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public class OllieProcessor extends AbstractProcessor {
	@Override
	public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
		return false;
	}
}