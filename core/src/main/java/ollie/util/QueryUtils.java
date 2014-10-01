package ollie.util;

import android.os.CancellationSignal;
import ollie.Model;
import ollie.Ollie;

import java.util.List;

import static android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Utility class for interacting with the database using SQLiteDatabase methods.
 */
public class QueryUtils {
	public static void execSQL(String sql) {
		Ollie.getDatabase().execSQL(sql);
	}

	public static void execSQL(String sql, String[] selectionArgs) {
		Ollie.getDatabase().execSQL(sql, selectionArgs);
	}

	public static <T extends Model> List<T> query(Class<T> cls, boolean distinct, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {

		return Ollie.processAndCloseCursor(cls, Ollie.getDatabase().query(distinct, Ollie.getTableName(cls), columns,
				selection, selectionArgs, groupBy, having, orderBy, limit));
	}

	public static <T extends Model> List<T> query(Class<T> cls, boolean distinct, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy, String limit,
			CancellationSignal cancellationSignal) {

		return Ollie.processAndCloseCursor(cls, Ollie.getDatabase().query(distinct, Ollie.getTableName(cls), columns,
				selection, selectionArgs, groupBy, having, orderBy, limit, cancellationSignal));
	}

	public static <T extends Model> List<T> queryWithFactory(Class<T> cls, CursorFactory cursorFactory,
			boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit) {

		return Ollie.processAndCloseCursor(cls, Ollie.getDatabase().queryWithFactory(cursorFactory, distinct,
				Ollie.getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy, limit));
	}

	public static <T extends Model> List<T> queryWithFactory(Class<T> cls, CursorFactory cursorFactory,
			boolean distinct, String[] columns, String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy, String limit, CancellationSignal cancellationSignal) {

		return Ollie.processAndCloseCursor(cls, Ollie.getDatabase().queryWithFactory(cursorFactory, distinct,
				Ollie.getTableName(cls), columns, selection, selectionArgs, groupBy, having, orderBy, limit,
				cancellationSignal));
	}

	public static <T extends Model> List<T> query(Class<T> cls, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy) {

		return Ollie.processAndCloseCursor(cls, Ollie.getDatabase().query(Ollie.getTableName(cls), columns, selection,
				selectionArgs, groupBy, having, orderBy));
	}

	public static <T extends Model> List<T> query(Class<T> cls, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {

		return Ollie.processAndCloseCursor(cls, Ollie.getDatabase().query(Ollie.getTableName(cls), columns, selection,
				selectionArgs, groupBy, having, orderBy, limit));
	}

	public static <T extends Model> List<T> rawQuery(Class<T> cls, String sql, String[] selectionArgs) {
		return Ollie.processAndCloseCursor(cls, Ollie.getDatabase().rawQuery(sql, selectionArgs));
	}

	public static <T extends Model> List<T> rawQuery(Class<T> cls, String sql, String[] selectionArgs,
			CancellationSignal cancellationSignal) {

		return Ollie.processAndCloseCursor(cls, Ollie.getDatabase().rawQuery(sql, selectionArgs, cancellationSignal));
	}

	public static <T extends Model> List<T> rawQueryWithFactory(Class<T> cls, CursorFactory cursorFactory, String sql,
			String[] selectionArgs, String editTable) {

		return Ollie.processAndCloseCursor(cls, Ollie.getDatabase().rawQueryWithFactory(cursorFactory, sql,
				selectionArgs, editTable));
	}

	public static <T extends Model> List<T> rawQueryWithFactory(Class<T> cls, CursorFactory cursorFactory, String sql,
			String[] selectionArgs, String editTable, CancellationSignal cancellationSignal) {

		return Ollie.processAndCloseCursor(cls, Ollie.getDatabase().rawQueryWithFactory(cursorFactory, sql,
				selectionArgs, editTable, cancellationSignal));
	}
}
