package ollie;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.SparseArray;
import ollie.internal.ModelAdapter;

public abstract class OllieProvider extends ContentProvider {
	private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	private static final SparseArray<Class<? extends Model>> TYPE_CODES = new SparseArray<Class<? extends Model>>();

	private static boolean sIsImplemented = false;
	private static String sAuthority;
	private static SparseArray<String> sMimeTypeCache = new SparseArray<String>();

	public static boolean isImplemented() {
		return sIsImplemented;
	}

	public static Uri createUri(Class<? extends Model> type, Long id) {
		final StringBuilder uri = new StringBuilder();
		uri.append("content://");
		uri.append(sAuthority);
		uri.append("/");
		uri.append(Ollie.getTableName(type).toLowerCase());

		if (id != null) {
			uri.append("/");
			uri.append(id.toString());
		}

		return Uri.parse(uri.toString());
	}

	@Override
	public boolean onCreate() {
		Ollie.init(getContext(), getDatabaseName(), getDatabaseVersion(), getCacheSize());
		sAuthority = getAuthority();
		sIsImplemented = true;

		int i = 0;
		for (ModelAdapter modelAdapter : Ollie.getModelAdapters()) {
			final int tableKey = (i * 2) + 1;
			final int itemKey = (i * 2) + 2;

			// content://<authority>/<table>
			URI_MATCHER.addURI(sAuthority, modelAdapter.getTableName().toLowerCase(), tableKey);
			TYPE_CODES.put(tableKey, modelAdapter.getModelType());

			// content://<authority>/<table>/<id>
			URI_MATCHER.addURI(sAuthority, modelAdapter.getTableName().toLowerCase() + "/#", itemKey);
			TYPE_CODES.put(itemKey, modelAdapter.getModelType());

			i++;
		}

		return true;
	}

	@Override
	public String getType(Uri uri) {
		final int match = URI_MATCHER.match(uri);

		String cachedMimeType = sMimeTypeCache.get(match);
		if (cachedMimeType != null) {
			return cachedMimeType;
		}

		final Class<? extends Model> type = getModelType(uri);
		final boolean single = ((match % 2) == 0);

		StringBuilder mimeType = new StringBuilder();
		mimeType.append("vnd");
		mimeType.append(".");
		mimeType.append(sAuthority);
		mimeType.append(".");
		mimeType.append(single ? "item" : "dir");
		mimeType.append("/");
		mimeType.append("vnd");
		mimeType.append(".");
		mimeType.append(sAuthority);
		mimeType.append(".");
		mimeType.append(Ollie.getTableName(type));

		sMimeTypeCache.append(match, mimeType.toString());

		return mimeType.toString();
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final Class<? extends Model> type = getModelType(uri);
		final Long id = Ollie.getDatabase().insert(Ollie.getTableName(type), null, values);

		if (id != null && id > 0) {
			Uri retUri = createUri(type, id);
			getContext().getContentResolver().notifyChange(uri, null);
			return retUri;
		}

		return null;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		final int count = Ollie.getDatabase().update(
				Ollie.getTableName(getModelType(uri)),
				values,
				selection,
				selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		final int count = Ollie.getDatabase().delete(
				Ollie.getTableName(getModelType(uri)),
				selection,
				selectionArgs);

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		final Cursor cursor = Ollie.getDatabase().query(
				Ollie.getTableName(getModelType(uri)),
				projection,
				selection,
				selectionArgs,
				null,
				null,
				sortOrder);

		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	protected abstract String getDatabaseName();

	protected abstract int getDatabaseVersion();

	protected String getAuthority() {
		return getContext().getPackageName();
	}

	protected int getCacheSize() {
		return Ollie.DEFAULT_CACHE_SIZE;
	}

	private Class<? extends Model> getModelType(Uri uri) {
		final int code = URI_MATCHER.match(uri);
		if (code != UriMatcher.NO_MATCH) {
			return TYPE_CODES.get(code);
		}
		return null;
	}
}
