package com.example.ollie.shadows;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

/**
 * Shadow for {@code SQLiteOpenHelper}.  Provides basic support for retrieving
 * databases and partially implements the subclass contract.  (Currently,
 * support for {@code #onUpgrade} is missing).
 */
@Implements(SQLiteOpenHelper.class)
public class PersistentShadowSQLiteOpenHelper {
	@RealObject
	private SQLiteOpenHelper mRealHelper;
	private static SQLiteDatabase mDatabase;
	private CursorFactory mFactory;

	public void __constructor__(Context context, String name, CursorFactory factory, int version) {
		if (mDatabase != null) {
			mDatabase.close();
		}
		mDatabase = null;
		mFactory = factory;
	}

	@Implementation
	public synchronized void close() {
		if (mDatabase != null) {
			mDatabase.close();
		}
		mDatabase = null;
	}

	@Implementation
	public synchronized SQLiteDatabase getReadableDatabase() {
		if (mDatabase == null) {
			mDatabase = SQLiteDatabase.openDatabase("path", mFactory, 0);
			mRealHelper.onCreate(mDatabase);
		}

		mRealHelper.onOpen(mDatabase);
		return mDatabase;
	}

	@Implementation
	public synchronized SQLiteDatabase getWritableDatabase() {
		if (mDatabase == null) {
			mDatabase = SQLiteDatabase.openDatabase("path", mFactory, 0);
			mRealHelper.onCreate(mDatabase);
		}

		mRealHelper.onOpen(mDatabase);
		return mDatabase;
	}
}
