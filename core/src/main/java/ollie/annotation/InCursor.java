package ollie.annotation;

import ollie.cursor_name_resolver.CursorNameResolver;
import ollie.cursor_name_resolver.UnderscoreCursorNameResolver;

import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * <p>
 * An annotation that indicates this relationship can be found in the same {@link android.database.Cursor} as the
 * original model. Takes an implementation of {@link ollie.cursor_name_resolver.CursorNameResolver}
 * which is used to resolve the name of the relationship's columns in the {@link android.database.Cursor}.
 * Must be used in conjunction with {@link ollie.annotation.Column}.
 * </p>
 */
@Target (FIELD)
@Retention (CLASS)
public @interface InCursor {
	/**
	 * Used to identify the name of this relationship's columns in the Cursor.
	 *
	 * @return The resolver implementation.
	 */
	public Class<? extends CursorNameResolver> value() default UnderscoreCursorNameResolver.class;
}
