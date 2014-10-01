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

/**
 * <p>
 * Type adapters provide a method for which to convert between Java types and SQLite types.
 * </p>
 * <p>
 * SQLite can only store primitive values in columns. Complex Java data types must, therefore, be converted to a
 * primitive SQLite type. The SQLite types are:
 * </p>
 * <ul>
 * <li>NULL</li>
 * <li>INTEGER</li>
 * <li>REAL</li>
 * <li>TEXT</li>
 * <li>BLOB</li>
 * </ul>
 * <a href="http://www.sqlite.org/datatype3.html">http://www.sqlite.org/datatype3.html</a>
 *
 * @param <D> Deserialized type, i.e. the Java type.
 * @param <S> Serialized type, i.e. the SQLite type.
 * @see ollie.adapter.BooleanAdapter
 * @see ollie.adapter.CalendarAdapter
 * @see ollie.adapter.SqlDateAdapter
 * @see ollie.adapter.UtilDateAdapter
 */
@ollie.internal.TypeAdapter
public abstract class TypeAdapter<D, S> {
	/**
	 * Converts the Java value to a value which SQLite can store.
	 *
	 * @param value The Java (deserialized) value.
	 * @return The SQLite value, converted from the Java value.
	 */
	public abstract S serialize(D value);

	/**
	 * Converts the SQLite value to the Java representation of that value.
	 *
	 * @param value The SQLite (serialized) value.
	 * @return The Java value, converted from the SQLite value.
	 */
	public abstract D deserialize(S value);
}