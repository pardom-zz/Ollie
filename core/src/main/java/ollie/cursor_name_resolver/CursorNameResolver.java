package ollie.cursor_name_resolver;

import ollie.Model;

/**
 * <p>
 * A helper that shows how to retrieve the columns for a nested relationship from
 * a {@link android.database.Cursor}.
 * </p>
 */
public interface CursorNameResolver {
	/**
	 * Gets the name of the column for a particular model in a Cursor.
	 *
	 * @param model The name of the model.
	 * @param column The name of the column.
	 * @return The name of the column in the Cursor.
	 */
	public String getCursorName(Class<? extends Model> model, String column);
}
