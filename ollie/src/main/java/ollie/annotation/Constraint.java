package ollie.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Target(ANNOTATION_TYPE)
@Retention(CLASS)
public @interface Constraint {
	public String value();
}
