/*
 * Copyright (C) 2014 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ollie;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.CancellationSignal;
import android.provider.BaseColumns;
import android.util.Log;
import ollie.internal.AdapterHolder;
import ollie.internal.ModelAdapter;
import ollie.util.LruCache;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public final class Ollie {
	public static final int DEFAULT_CACHE_SIZE = 1024;

	private static final String TAG = "Ollie";

	private static Context sContext;
	private static AdapterHolder sAdapterHolder;
	private static DatabaseHelper sDatabaseHelper;
	private static SQLiteDatabase sSQLiteDatabase;
	private static LruCache<String, Model> sCache;
	private static LogLevel sLogLevel = LogLevel.NONE;
	private static boolean sInitialized = false;

	public enum LogLevel {
		/**
		 * No logging.
		 */
		NONE,
		/**
		 * Log basic events.
		 */
		BASIC,
		/**
		 * Log all queries.
		 */
		FULL;

		public boolean log(LogLevel logLevel) {
			return this.ordinal() >= logLevel.ordinal();
		}
	}

	private Ollie() {
	}

	// Public methods

	public static void init(Context context, String name, int version) {
		init(context, name, version, DEFAULT_CACHE_SIZE, LogLevel.NONE);
	}

	public static void init(Context context, String name, int version, int cacheSize) {
		init(context, name, version, cacheSize, LogLevel.NONE);
	}

	public static void init(Context context, String name, int version, LogLevel logLevel) {
		init(context, name, version, DEFAULT_CACHE_SIZE, logLevel);
	}

	public static void init(Context context, String name, int version, int cacheSize, LogLevel logLevel) {
		sLogLevel = logLevel;

		if (sInitialized) {
			if (sLogLevel.log(LogLevel.BASIC)) {
				Log.d(TAG, "Already initialized.");
			}
			return;
		}

		try {
			Class adapterClass = Class.forName(AdapterHolder.IMPL_CLASS_FQCN);
			sAdapterHolder = (AdapterHolder) adapterClass.newInstance();
		} catch (Exception e) {
			if (sLogLevel.log(LogLevel.BASIC)) {
				Log.e(TAG, "Failed to initialize.", e);
			}
		}

		sContext = context.getApplicationContext();
		sDatabaseHelper = new DatabaseHelper(sContext, name, version);
		sSQLiteDatabase = sDatabaseHelper.getWritableDatabase();
		sCache = new LruCache<String, Model>(cacheSize);

		sInitialized = true;
	}

	public static Context getContext() {
		return sContext;
	}

	public static SQLiteDatabase getDatabase() {
		return sSQLiteDatabase;
	}

	public static <T extends Model> String getTableName(Class<T> cls) {
		return sAdapterHolder.getModelAdapter(cls).getTableName();
	}

	// Query wrappers

	public static void execSQL(String sql) {
		sSQLiteDatabase.execSQL(sql);
	}

	public static void execSQL(String sql, String[] selectionArgs) {
		sSQLiteDatabase.execSQL(sql, selectionArgs);
	}

	public static <T extends Model> List<T> query(Class<T> cls, boolean distinct, String[] columns, String selection,
												  String[] selectionArgs, String groupBy, String having,
												  String orderBy, String limit) {
		return processAndCloseCursor(cls, sSQLiteDatabase.query(distinct, getTableName(cls), columns, selection,
				selectionArgs, groupBy, having, orderBy, limit));
	}

	@TargetApi(VERSION_CODES.JELLY_BEAN)
	public static <T extends Model> List<T> query(Class<T> cls, boolean distinct, String[] columns, String selection,
												  String[] selectionArgs, String groupBy, String having,
												  String orderBy, String limit, CancellationSignal
			cancellationSignal) {
		return processAndCloseCursor(cls, sSQLiteDatabase.query(distinct, getTableName(cls), columns, selection,
				selectionArgs, groupBy, having, orderBy, limit, cancellationSignal));
	}

	public static <T extends Model> List<T> queryWithFactory(Class<T> cls, CursorFactory cursorFactory,
															 boolean distinct, String[] columns, String selection,
															 String[] selectionArgs, String groupBy, String having,
															 String orderBy, String limit) {
		return processAndCloseCursor(cls, sSQLiteDatabase.queryWithFactory(cursorFactory, distinct, getTableName(cls),
				columns, selection, selectionArgs, groupBy, having, orderBy, limit));
	}

	@TargetApi(VERSION_CODES.JELLY_BEAN)
	public static <T extends Model> List<T> queryWithFactory(Class<T> cls, CursorFactory cursorFactory,
															 boolean distinct, String[] columns, String selection,
															 String[] selectionArgs, String groupBy, String having,
															 String orderBy, String limit, CancellationSignal
			cancellationSignal) {
		return processAndCloseCursor(cls, sSQLiteDatabase.queryWithFactory(cursorFactory, distinct, getTableName(cls),
				columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal));
	}

	public static <T extends Model> List<T> query(Class<T> cls, String[] columns, String selection,
												  String[] selectionArgs, String groupBy, String having,
												  String orderBy) {
		return processAndCloseCursor(cls, sSQLiteDatabase.query(getTableName(cls), columns, selection, selectionArgs,
				groupBy, having, orderBy));
	}

	public static <T extends Model> List<T> query(Class<T> cls, String[] columns, String selection,
												  String[] selectionArgs, String groupBy, String having,
												  String orderBy, String limit) {
		return processAndCloseCursor(cls, sSQLiteDatabase.query(getTableName(cls), columns, selection, selectionArgs,
				groupBy, having, orderBy, limit));
	}

	public static <T extends Model> List<T> rawQuery(Class<T> cls, String sql, String[] selectionArgs) {
		return processAndCloseCursor(cls, sSQLiteDatabase.rawQuery(sql, selectionArgs));
	}

	@TargetApi(VERSION_CODES.JELLY_BEAN)
	public static <T extends Model> List<T> rawQuery(Class<T> cls, String sql, String[] selectionArgs,
													 CancellationSignal cancellationSignal) {
		return processAndCloseCursor(cls, sSQLiteDatabase.rawQuery(sql, selectionArgs, cancellationSignal));
	}

	public static <T extends Model> List<T> rawQueryWithFactory(Class<T> cls, CursorFactory cursorFactory, String sql,
																String[] selectionArgs, String editTable) {
		return processAndCloseCursor(cls, sSQLiteDatabase.rawQueryWithFactory(cursorFactory, sql, selectionArgs,
				editTable));
	}

	@TargetApi(VERSION_CODES.JELLY_BEAN)
	public static <T extends Model> List<T> rawQueryWithFactory(Class<T> cls, CursorFactory cursorFactory, String sql,
																String[] selectionArgs, String editTable,
																CancellationSignal cancellationSignal) {
		return processAndCloseCursor(cls, sSQLiteDatabase.rawQueryWithFactory(cursorFactory, sql, selectionArgs,
				editTable, cancellationSignal));
	}

	// Finder methods

	static List<String> getTableDefinitions() {
		List<String> tableDefinitions = new ArrayList<String>();
		for (ModelAdapter modelAdapter : sAdapterHolder.getModelAdapters()) {
			tableDefinitions.add(modelAdapter.getSchema());
		}

		return tableDefinitions;
	}

	static <D, S> TypeAdapter<D, S> getTypeAdapter(Class<D> cls) {
		return (TypeAdapter<D, S>) sAdapterHolder.getTypeAdapter(cls);
	}

	static List<? extends ModelAdapter> getModelAdapters() {
		return sAdapterHolder.getModelAdapters();
	}

	static List<? extends Migration> getMigrations() {
		return sAdapterHolder.getMigrations();
	}

	// Cache methods

	static synchronized <T extends Model> void putEntity(T entity) {
		if (entity.id != null) {
			sCache.put(getEntityIdentifier(entity.getClass(), entity.id), entity);
		}
	}

	static synchronized <T extends Model> T getEntity(Class<T> cls, long id) {
		return (T) sCache.get(getEntityIdentifier(cls, id));
	}

	static synchronized <T extends Model> void removeEntity(T entity) {
		sCache.remove(getEntityIdentifier(entity.getClass(), entity.id));
	}

	static synchronized <T extends Model> T getOrFindEntity(Class<T> cls, long id) {
		T entity = Ollie.getEntity(cls, id);
		if (entity == null) {
			entity = Model.find(cls, id);
		}
		return entity;
	}

	// Model adapter methods

	static synchronized <T extends Model> void load(T entity, Cursor cursor) {
		sAdapterHolder.getModelAdapter(entity.getClass()).load(entity, cursor);
	}

	static synchronized <T extends Model> Long save(T entity) {
		return sAdapterHolder.getModelAdapter(entity.getClass()).save(entity, sSQLiteDatabase);
	}

	static synchronized <T extends Model> void delete(T entity) {
		sAdapterHolder.getModelAdapter(entity.getClass()).delete(entity, sSQLiteDatabase);
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

	// Private classes

	private static final class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context, String name, int version) {
			super(context, name, sLogLevel.log(LogLevel.FULL) ? new LoggingCursorAdapter() : null, version);
		}

		@Override
		public void onOpen(SQLiteDatabase db) {
			executePragmas(db);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			executePragmas(db);
			executeCreate(db);
			executeMigrations(db, -1, db.getVersion());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			executePragmas(db);
			executeCreate(db);
			executeMigrations(db, oldVersion, newVersion);
		}

		private void executePragmas(SQLiteDatabase db) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				db.execSQL("PRAGMA foreign_keys=ON;");
			}
		}

		private void executeCreate(SQLiteDatabase db) {
			db.beginTransaction();
			try {
				for (String tableDefinition : Ollie.getTableDefinitions()) {
					db.execSQL(tableDefinition);
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		}

		private boolean executeMigrations(SQLiteDatabase db, int oldVersion, int newVersion) {
			boolean migrationExecuted = false;
			final List<? extends Migration> migrations = Ollie.getMigrations();

			db.beginTransaction();
			try {
				for (Migration migration : migrations) {
					if (migration.getVersion() > oldVersion && migration.getVersion() <= newVersion) {
						for (String statement : migration.getStatements()) {
							db.execSQL(statement);
						}
						migrationExecuted = true;
					}
				}
			} finally {
				db.setTransactionSuccessful();
			}
			db.endTransaction();

			return migrationExecuted;
		}
	}

	private static final class LoggingCursorAdapter implements CursorFactory {
		@Override
		public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable, SQLiteQuery query) {
			Log.v(TAG, query.toString());
			return new SQLiteCursor(db, driver, editTable, query);
		}
	}
}