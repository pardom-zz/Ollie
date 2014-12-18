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

package ollie.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * <p>
 * An annotation that indicates a class is indexed. Requires the index name and column or columns to be specified.
 * </p>
 * <p>
 * <a href="https://www.sqlite.org/lang_createindex.html">https://www.sqlite.org/lang_createindex.html</a>
 * </p>
 */
@Target(TYPE)
@Retention(CLASS)
public @interface Index {
	/**
	 * Returns the index name.
	 *
	 * @return The index name.
	 */
	public String name();

	/**
	 * The column or columns to index.
	 *
	 * @return The index column name or names.
	 */
	public String[] columns();

	/**
	 * Mark this index as unique, i.e. no duplcates are allowed.
	 *
	 * @return Whether the index is unique. False by default.
	 */
	public boolean unique() default false;

	/**
	 * Optional clause to create a partial index.
	 *
	 * @return The partial index clause.
	 */
	public String condition() default "";
}
