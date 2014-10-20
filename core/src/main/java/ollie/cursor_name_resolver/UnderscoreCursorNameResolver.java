package ollie.cursor_name_resolver;

import ollie.Model;
import ollie.Ollie;

/**
 * <p>
 * An implementation of {@link ollie.cursor_name_resolver.CursorNameResolver} that
 * simply uses the pattern `{table_name}_{column_name}`.
 * </p>
 */
public class UnderscoreCursorNameResolver implements CursorNameResolver {
	@Override
	public String getCursorName ( Class<? extends Model> model, String column ) {
		return Ollie.getTableName(model) + "_" + column;
	}
}
