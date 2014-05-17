package ollie;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.os.Build.VERSION_CODES;
import android.os.CancellationSignal;
import android.provider.BaseColumns;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ollie.internal.AdapterHolder;
import ollie.internal.ModelAdapter;
import ollie.util.LruCache;

public final class Ollie {
    public static final int DEFAULT_CACHE_SIZE = 1024;

    private static final String TAG = "Ollie";

    private static Context sContext;
    private static AdapterHolder sAdapterHolder;
    private static DatabaseHelper sDatabaseHelper;
    private static SQLiteDatabase sSQLiteDatabase;
    private static LruCache<String, Model> sCache;
    private static boolean sInitialized = false;

    private Ollie() {
    }

    // Public methods

    public static void init(Context context, String name, int version) {
        init(context, name, version, DEFAULT_CACHE_SIZE);
    }

    public static void init(Context context, String name, int version, int cacheSize) {
        if (sInitialized) {
            return;
        }

        try {
            Class adapterClass = Class.forName(AdapterHolder.IMPLEMENTATION_CLASS_FQCN);
            sAdapterHolder = (AdapterHolder) adapterClass.newInstance();
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize.", e);
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

    public static <T extends Model> ModelAdapter getModelAdapter(Class<T> cls){
        return sAdapterHolder.getModelAdapter(cls);
    }

    // Query wrappers

    public static <T extends Model> List<T> query(Class<T> cls, boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return processAndCloseCursor(cls, sSQLiteDatabase.query(distinct, getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy, limit));
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    public static <T extends Model> List<T> query(Class<T> cls, boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal) {
        return processAndCloseCursor(cls, sSQLiteDatabase.query(distinct, getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal));
    }

    public static <T extends Model> List<T> queryWithFactory(Class<T> cls, CursorFactory cursorFactory, boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return processAndCloseCursor(cls, sSQLiteDatabase.queryWithFactory(cursorFactory, distinct, getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy, limit));
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    public static <T extends Model> List<T> queryWithFactory(Class<T> cls, CursorFactory cursorFactory, boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit, CancellationSignal cancellationSignal) {
        return processAndCloseCursor(cls, sSQLiteDatabase.queryWithFactory(cursorFactory, distinct, getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal));
    }

    public static <T extends Model> List<T> query(Class<T> cls, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return processAndCloseCursor(cls, sSQLiteDatabase.query(getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy));
    }

    public static <T extends Model> List<T> query(Class<T> cls, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return processAndCloseCursor(cls, sSQLiteDatabase.query(getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy, limit));
    }

    public static <T extends Model> List<T> rawQuery(Class<T> cls, String sql, String[] selectionArgs) {
        return processAndCloseCursor(cls, sSQLiteDatabase.rawQuery(sql, selectionArgs));
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    public static <T extends Model> List<T> rawQuery(Class<T> cls, String sql, String[] selectionArgs, CancellationSignal cancellationSignal) {
        return processAndCloseCursor(cls, sSQLiteDatabase.rawQuery(sql, selectionArgs, cancellationSignal));
    }

    public static <T extends Model> List<T> rawQueryWithFactory(Class<T> cls, CursorFactory cursorFactory, String sql, String[] selectionArgs, String editTable) {
        return processAndCloseCursor(cls, sSQLiteDatabase.rawQueryWithFactory(cursorFactory, sql, selectionArgs, editTable));
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    public static <T extends Model> List<T> rawQueryWithFactory(Class<T> cls, CursorFactory cursorFactory, String sql, String[] selectionArgs, String editTable, CancellationSignal cancellationSignal) {
        return processAndCloseCursor(cls, sSQLiteDatabase.rawQueryWithFactory(cursorFactory, sql, selectionArgs, editTable, cancellationSignal));
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

//    static boolean isPolymorph(Class cls) {
//        return sAdapterHolder.getPolymorphicTables().contains(cls);
//    }
//
//    static boolean isSubtype(Class cls) {
//        return sAdapterHolder.getSubtypeMap().containsKey(cls);
//    }
//
//    static String getSubtypeName(Class cls) {
//        return sAdapterHolder.getSubtypeMap().get(cls);
//    }

//    static <T extends Model> Class<T> getSubtypeClass(Class polymorphClass, String name) {
//        for (Map.Entry<Class<? extends Model>, String> e : sAdapterHolder.getSubtypeMap().entrySet()) {
//            Class subtypeClass = e.getKey();
//            String subtypeName = e.getValue();
//
//            if (name.equals(subtypeName)) {
//                if (subtypeClass.getSuperclass() == polymorphClass) {
//                    return subtypeClass;
//                }
//            }
//        }
//        return null;
//    }

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
            if (cursor.moveToFirst()) {
                do {
                    ModelAdapter adapter = getModelAdapter(cls);
                    String type = adapter.isPolymorphic() ?
                            cursor.getString(cursor.getColumnIndex(adapter.getTypeColumn())) : null;

                    T entity = getEntity(cls, cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
                    if (entity == null) {
                        entity = (T) adapter.newEntity(type);
                        if (entity == null) continue;
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