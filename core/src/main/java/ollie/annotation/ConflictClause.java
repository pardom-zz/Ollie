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

/**
 * <p>
 * An annotation that indicates a constraint should use a specified behavior upon encountering a conflict. May be used
 * with {@link ollie.annotation.NotNull} and {@link ollie.annotation.Unique}.
 * </p>
 * <p>
 * <a href="http://www.sqlite.org/lang_conflict.html">http://www.sqlite.org/lang_conflict.html</a>
 * <a href="http://www.sqlite.org/syntaxdiagrams.html#column-constraint">
 * http://www.sqlite.org/syntaxdiagrams.html#column-constraint
 * </a>
 * </p>
 */
public enum ConflictClause {
	/**
	 * Do not specify a conflict clause.
	 */
	NONE(null),
	/**
	 * When an applicable constraint violation occurs, the ROLLBACK resolution algorithm aborts the current SQL
	 * statement with an SQLITE_CONSTRAINT error and rolls back the current transaction. If no transaction is active
	 * (other than the implied transaction that is created on every command) then the ROLLBACK resolution algorithm
	 * works the same as the ABORT algorithm.
	 */
	ROLLBACK("ROLLBACK"),
	/**
	 * When an applicable constraint violation occurs, the ABORT resolution algorithm aborts the current SQL statement
	 * with an SQLITE_CONSTRAINT error and backs out any changes made by the current SQL statement; but changes caused
	 * by prior SQL statements within the same transaction are preserved and the transaction remains active. This is
	 * the default behavior and the behavior specified by the SQL standard.
	 */
	ABORT("ABORT"),
	/**
	 * When an applicable constraint violation occurs, the FAIL resolution algorithm aborts the current SQL statement
	 * with an SQLITE_CONSTRAINT error. But the FAIL resolution does not back out prior changes of the SQL statement
	 * that failed nor does it end the transaction. For example, if an UPDATE statement encountered a constraint
	 * violation on the 100th row that it attempts to update, then the first 99 row changes are preserved but changes
	 * to rows 100 and beyond never occur.
	 */
	FAIL("FAIL"),
	/**
	 * When an applicable constraint violation occurs, the IGNORE resolution algorithm skips the one row that contains
	 * the constraint violation and continues processing subsequent rows of the SQL statement as if nothing went wrong
	 * . Other rows before and after the row that contained the constraint violation are inserted or updated normally.
	 * No error is returned when the IGNORE conflict resolution algorithm is used.
	 */
	IGNORE("IGNORE"),
	/**
	 * <p>
	 * When a UNIQUE or PRIMARY KEY constraint violation occurs, the REPLACE algorithm deletes pre-existing rows that
	 * are causing the constraint violation prior to inserting or updating the current row and the command continues
	 * executing normally. If a NOT NULL constraint violation occurs, the REPLACE conflict resolution replaces the
	 * NULL value with the default value for that column, or if the column has no default value,
	 * then the ABORT algorithm is used. If a CHECK constraint violation occurs, the REPLACE conflict resolution
	 * algorithm always works like ABORT.
	 * </p>
	 * <p>
	 * When the REPLACE conflict resolution strategy deletes rows in order to satisfy a constraint,
	 * delete triggers fire if and only if recursive triggers are enabled.
	 * </p>
	 * <p>
	 * The update hook is not invoked for rows that are deleted by the REPLACE conflict resolution strategy. Nor does
	 * REPLACE increment the change counter. The exceptional behaviors defined in this paragraph might change in a
	 * future release.
	 * </p>
	 */
	REPLACE("REPLACE");

	private String mKeyword;

	ConflictClause(String keyword) {
		mKeyword = keyword;
	}

	/**
	 * Returns a keyword string for the conflict clause.
	 *
	 * @return The keyword.
	 */
	public String keyword() {
		return mKeyword;
	}
}