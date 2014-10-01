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
 * An annotation that indicates a class is a table. Requires the table name to be specified.
 * </p>
 * <p>
 * <a href="http://www.sqlite.org/lang_createtable.html">http://www.sqlite.org/lang_createtable.html</a>
 * </p>
 */
@Target(TYPE)
@Retention(CLASS)
public @interface Table {
	/**
	 * Returns the table name.
	 *
	 * @return The table name.
	 */
	public String value();
}
