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

package ollie.test.shadows;

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
