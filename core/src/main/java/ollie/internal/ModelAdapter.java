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

package ollie.internal;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import ollie.Model;

/**
 * Used internally to perform database operations on a model.
 */
public abstract class ModelAdapter<T extends Model> {
	public abstract Class<? extends Model> getModelType();

	public abstract String getTableName();

	public abstract String getSchema();

	public abstract void load(T entity, Cursor cursor);

	public abstract Long save(T entity, SQLiteDatabase db);

	public abstract void delete(T entity, SQLiteDatabase db);

	protected final Long insertOrUpdate(T entity, SQLiteDatabase db, ContentValues values) {
        entity.id = db.replace(getTableName(), null, values);

		return entity.id;
	}
}
