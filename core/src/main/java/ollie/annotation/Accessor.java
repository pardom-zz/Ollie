package ollie.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * <p>
 * An annotation that indicates a member is an accessor or mutator for a table column.
 * </p>
 */
@Target (METHOD)
@Retention (CLASS)
public @interface Accessor {
	/**
	 * Returns the column name.
	 *
	 * @return The column name.
	 */
	public String value();
}
