package ollie.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * <p>
 * An annotation that indicates a member should be retrieved and updated. Must be
 * used in conjunction with {@link ollie.annotation.Column}
 * </p>
 */
@Target (FIELD)
@Retention (CLASS)
public @interface GetSet {
	/**
	 * Returns the method name to call when retrieving this field.
	 * If the default, empty or null, will guess at the name based on a standard.
	 *
	 * @return The method name to call.
	 */
	public String getMethodName() default "";

	/**
	 * Returns the method name to call when setting this field.
	 * If the default, empty or null, will guess at the name based on a standard.
	 *
	 * @return The method name to call.
	 */
	public String setMethodName() default "";
}
