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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * <p>
 * An annotation that indicates a member should define its SQLite column using the FOREIGN KEY constraint. Must be used
 * in conjunction with {@link ollie.annotation.Column}.
 * </p>
 * <p>
 * <a href="http://www.sqlite.org/foreignkeys.html">http://www.sqlite.org/foreignkeys.html</a>
 * <a href="http://www.sqlite.org/syntaxdiagrams.html#column-def">
 * http://www.sqlite.org/syntaxdiagrams.html#column-def
 * </a>
 * </p>
 */
@Target(FIELD)
@Retention(CLASS)
public @interface ForeignKey {
	/**
	 * <p>
	 * Perform an action on referenced rows on update and delete.
	 * </p>
	 * <a href="http://www.sqlite.org/foreignkeys.html#fk_actions">
	 * http://www.sqlite.org/foreignkeys.html#fk_actions
	 * </a>
	 */
	public enum ReferentialAction {
		/**
		 * No action.
		 */
		NONE(null),
		/**
		 * If the configured action is "SET NULL", then when a parent key is deleted (for ON DELETE SET NULL) or
		 * modified (for ON UPDATE SET NULL), the child key columns of all rows in the child table that mapped to the
		 * parent key are set to contain SQL NULL values.
		 */
		SET_NULL("SET NULL"),
		/**
		 * The "SET DEFAULT" actions are similar to "SET NULL", except that each of the child key columns is set to
		 * contain the columns default value instead of NULL. Refer to the CREATE TABLE documentation for details on
		 * how default values are assigned to table columns.
		 */
		SET_DEFAULT("SET DEFAULT"),
		/**
		 * A "CASCADE" action propagates the delete or update operation on the parent key to each dependent child key.
		 * For an "ON DELETE CASCADE" action, this means that each row in the child table that was associated with the
		 * deleted parent row is also deleted. For an "ON UPDATE CASCADE" action, it means that the values stored in
		 * each dependent child key are modified to match the new parent key values.
		 */
		CASCADE("CASCADE"),
		/**
		 * The "RESTRICT" action means that the application is prohibited from deleting (for ON DELETE RESTRICT) or
		 * modifying (for ON UPDATE RESTRICT) a parent key when there exists one or more child keys mapped to it. The
		 * difference between the effect of a RESTRICT action and normal foreign key constraint enforcement is that
		 * the RESTRICT action processing happens as soon as the field is updated - not at the end of the current
		 * statement as it would with an immediate constraint, or at the end of the current transaction as it would
		 * with a deferred constraint. Even if the foreign key constraint it is attached to is deferred,
		 * configuring a RESTRICT action causes SQLite to return an error immediately if a parent key with dependent
		 * child keys is deleted or modified.
		 */
		RESTRICT("RESTRICT"),
		/**
		 * Configuring "NO ACTION" means just that: when a parent key is modified or deleted from the database,
		 * no special action is taken.
		 */
		NO_ACTION("NO ACTION");

		private String mKeyword;

		ReferentialAction(String keyword) {
			mKeyword = keyword;
		}

		/**
		 * Returns a keyword string for the referential action.
		 *
		 * @return The keyword.
		 */
		public String keyword() {
			return mKeyword;
		}
	}

	/**
	 * <p>
	 * If a statement modifies the contents of the database so that an immediate foreign key constraint is in
	 * violation at the conclusion the statement, an exception is thrown and the effects of the statement are
	 * reverted . By contrast, if a statement modifies the contents of the database such that a deferred foreign key
	 * constraint is violated, the violation is not reported immediately. Deferred foreign key constraints are not
	 * checked until the transaction tries to COMMIT. For as long as the user has an open transaction, the database is
	 * allowed to exist in a state that violates any number of deferred foreign key constraints. However, COMMIT will
	 * fail as long as foreign key constraints remain in violation.
	 * </p>
	 * <p>
	 * If the current statement is not inside an explicit transaction (a BEGIN/COMMIT/ROLLBACK block),
	 * then an implicit transaction is committed as soon as the statement has finished executing.
	 * </p>
	 */
	public enum Deferrable {
		NONE(null),
		DEFERRABLE("DEFERRABLE"),
		NOT_DEFERRABLE("NOT DEFERRABLE");

		private String mKeyword;

		Deferrable(String keyword) {
			mKeyword = keyword;
		}

		/**
		 * Returns a keyword string for the deferrable behavior.
		 *
		 * @return The keyword.
		 */
		public String keyword() {
			return mKeyword;
		}
	}

	public enum DeferrableTiming {
		NONE(null),
		DEFERRED("INITIALLY DEFERRED"),
		IMMEDIATE("INITIALLY IMMEDIATE");

		private String mKeyword;

		DeferrableTiming(String keyword) {
			mKeyword = keyword;
		}

		/**
		 * Returns a keyword string for the deferrable timing.
		 *
		 * @return The keyword.
		 */
		public String keyword() {
			return mKeyword;
		}

	}

	/**
	 * Optional set of columns to reference on the foreign table.
	 *
	 * @return The foreign column names.
	 */
	public String[] foreignColumns() default {};

	/**
	 * Referential action to perform upon deletion of this key.
	 *
	 * @return The referential action.
	 */
	public ReferentialAction onDelete() default ReferentialAction.NONE;

	/**
	 * Referential action to perform upon update of this key.
	 *
	 * @return The referential action.
	 */
	public ReferentialAction onUpdate() default ReferentialAction.NONE;

	/**
	 * Returns an enforcement behavior, deferred or immediate.
	 *
	 * @return The deferrable behaviour.
	 */
	public Deferrable deferrable() default Deferrable.NONE;

	/**
	 * Returns the initial enforcement behavior.
	 *
	 * @return The deferrable timing.
	 */
	public DeferrableTiming deferrableTiming() default DeferrableTiming.NONE;
}