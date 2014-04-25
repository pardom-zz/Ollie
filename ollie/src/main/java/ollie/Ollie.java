package ollie;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Build.VERSION_CODES;
import android.os.CancellationSignal;
import android.provider.BaseColumns;
import android.support.v4.util.LruCache;
import android.util.Log;
import ollie.internal.AdapterHolder;
import ollie.internal.ModelAdapter;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class Ollie {
	public static final int DEFAULT_CACHE_SIZE = 1024;

	static final String TAG = "Ollie";

	private static AdapterHolder sAdapterHolder;
	private static DatabaseHelper sDatabaseHelper;
	private static SQLiteDatabase sSQLiteDatabase;
	private static LruCache<String, Model> sCache;
	private static boolean sInitialized = false;

	private Ollie() {
		throw new AssertionError("No instances.");
	}

	// Public methods

	public static synchronized void init(Context context, String name, int version) {
		init(context, name, version, DEFAULT_CACHE_SIZE);
	}

	public static synchronized void init(Context context, String name, int version, int cacheSize) {
		if (sInitialized) {
			return;
		}

		try {
			Class adapterClass = Class.forName("ollie.AdapterHolderImpl");
			sAdapterHolder = (AdapterHolder) adapterClass.newInstance();
		} catch (Exception e) {
			Log.e(TAG, "Failed to initialize.", e);
		}

		sDatabaseHelper = new DatabaseHelper(context, name, version);
		sSQLiteDatabase = sDatabaseHelper.getWritableDatabase();
		sCache = new LruCache<String, Model>(cacheSize);

		sInitialized = true;
	}

	// Query wrappers

	public static synchronized <T extends Model> List<T> query(Class<T> cls, boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
		return processAndCloseCursor(cls, sSQLiteDatabase.query(distinct, getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy, limit));
	}

	@TargetApi(VERSION_CODES.JELLY_BEAN)
	public static synchronized <T extends Model> List<T> query(Class<T> cls, boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal) {
		return processAndCloseCursor(cls, sSQLiteDatabase.query(distinct, getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal));
	}

	public static synchronized <T extends Model> List<T> queryWithFactory(Class<T> cls, CursorFactory cursorFactory, boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
		return processAndCloseCursor(cls, sSQLiteDatabase.queryWithFactory(cursorFactory, distinct, getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy, limit));
	}

	@TargetApi(VERSION_CODES.JELLY_BEAN)
	public static synchronized <T extends Model> List<T> queryWithFactory(Class<T> cls, CursorFactory cursorFactory, boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal) {
		return processAndCloseCursor(cls, sSQLiteDatabase.queryWithFactory(cursorFactory, distinct, getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal));
	}

	public static synchronized <T extends Model> List<T> query(Class<T> cls, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		return processAndCloseCursor(cls, sSQLiteDatabase.query(getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy));
	}

	public static synchronized <T extends Model> List<T> query(Class<T> cls, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
		return processAndCloseCursor(cls, sSQLiteDatabase.query(getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy, limit));
	}

	public static synchronized <T extends Model> List<T> rawQuery(Class<T> cls, String sql, String[] selectionArgs) {
		return processAndCloseCursor(cls, sSQLiteDatabase.rawQuery(sql, selectionArgs));
	}

	@TargetApi(VERSION_CODES.JELLY_BEAN)
	public static synchronized <T extends Model> List<T> rawQuery(Class<T> cls, String sql, String[] selectionArgs, CancellationSignal cancellationSignal) {
		return processAndCloseCursor(cls, sSQLiteDatabase.rawQuery(sql, selectionArgs, cancellationSignal));
	}

	public static synchronized <T extends Model> List<T> rawQueryWithFactory(Class<T> cls, CursorFactory cursorFactory, String sql, String[] selectionArgs, String editTable) {
		return processAndCloseCursor(cls, sSQLiteDatabase.rawQueryWithFactory(cursorFactory, sql, selectionArgs, editTable));
	}

	@TargetApi(VERSION_CODES.JELLY_BEAN)
	public static synchronized <T extends Model> List<T> rawQueryWithFactory(Class<T> cls, CursorFactory cursorFactory, String sql, String[] selectionArgs, String editTable, CancellationSignal cancellationSignal) {
		return processAndCloseCursor(cls, sSQLiteDatabase.rawQueryWithFactory(cursorFactory, sql, selectionArgs, editTable, cancellationSignal));
	}

	// Package methods

	static synchronized <T extends Model> String getTableName(Class<T> cls) {
		return sAdapterHolder.getModelAdapter(cls).getTableName();
	}

	static synchronized List<String> getTableDefinitions() {
		List<String> tableDefinitions = new ArrayList<String>();
		for (ModelAdapter modelAdapter : sAdapterHolder.getModelAdapters()) {
			tableDefinitions.add(modelAdapter.getSchema());
		}

		return tableDefinitions;
	}

	static synchronized <D, S> TypeAdapter<D, S> getTypeAdapter(Class<D> cls) {
		return (TypeAdapter<D, S>) sAdapterHolder.getTypeAdapter(cls);
	}

	static synchronized <T extends Model> void load(T entity, Cursor cursor) {
		sAdapterHolder.getModelAdapter(entity.getClass()).load(entity, cursor);
	}

	static synchronized <T extends Model> Long save(T entity) {
		return sAdapterHolder.getModelAdapter(entity.getClass()).save(entity, sSQLiteDatabase);
	}

	static synchronized <T extends Model> void putEntity(T entity) {
		if (entity.id != null) {
			sCache.put(getEntityIdentifier(entity.getClass(), entity.id), entity);
		}
	}

	static synchronized <T extends Model> T getEntity(Class<T> cls, long id) {
		return (T) sCache.get(getEntityIdentifier(cls, id));
	}

	static synchronized <T extends Model> T getOrFindEntity(Class<T> cls, long id) {
		T entity = Ollie.getEntity(cls, id);
		if (entity == null) {
			entity = Model.find(cls, id);
		}
		return entity;
	}

	// Private methods

	private static String getEntityIdentifier(Class<? extends Model> cls, long id) {
		return cls.getName() + "@" + id;
	}

	private static <T extends Model> List<T> processAndCloseCursor(Class<T> cls, Cursor cursor) {
		List<T> entities = processCursor(cls, cursor);
		cursor.close();
		return entities;
	}

	private static <T extends Model> List<T> processCursor(Class<T> cls, Cursor cursor) {
		final List<T> entities = new ArrayList<T>();
		try {
			Constructor<T> entityConstructor = cls.getConstructor();
			if (cursor.moveToFirst()) {
				do {
					T entity = getEntity(cls, cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
					if (entity == null) {
						entity = entityConstructor.newInstance();
					}

					entity.load(cursor);
					entities.add(entity);
				}
				while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e(TAG, "Failed to process cursor.", e);
		}

		return entities;
	}
}