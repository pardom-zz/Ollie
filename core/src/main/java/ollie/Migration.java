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
 * Migrations execute SQL statements when upgrading from one database version to another. Migrations are sorted by
 * version number and executed in that order using migrations with versions higher than the current database version
 * number, and excluding all those with version numbers at or below the current database version number.
 */
@ollie.internal.Migration
public abstract class Migration implements Comparable<Migration> {
	/**
	 * Returns the database version for which to apply this migration.
	 *
	 * @return The database version.
	 */
	public abstract int getVersion();

	/**
	 * Returns the SQL statements which are to be executed in order to perform this migration.
	 *
	 * @return The SQL statements.
	 */
	public abstract String[] getStatements();

	@Override
	public int compareTo(Migration migration) {
		return Integer.compare(getVersion(), migration.getVersion());
	}
}
