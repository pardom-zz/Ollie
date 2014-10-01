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
	public enum ReferentialAction {
		NONE(null),
		SET_NULL("SET NULL"),
		SET_DEFAULT("SET DEFAULT"),
		CASCADE("CASCADE"),
		RESTRICT("RESTRICT"),
		NO_ACTION("NO ACTION");

		private String mKeyword;

		ReferentialAction(String keyword) {
			mKeyword = keyword;
		}

		public String keyword() {
			return mKeyword;
		}
	}

	public enum Deferrable {
		NONE(null),
		DEFERRABLE("DEFERRABLE"),
		NOT_DEFERRABLE("NOT DEFERRABLE");

		private String mKeyword;

		Deferrable(String keyword) {
			mKeyword = keyword;
		}

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

		public String keyword() {
			return mKeyword;
		}

	}

	/**
	 * <p>
	 * Optional set of columns to reference on the foreign table.
	 * </p>
	 *
	 * @return The foreign column names.
	 */
	public String[] foreignColumns() default {};

	/**
	 * <p>
	 * Referential action to perform upon deletion of this key.
	 * </p>
	 *
	 * @return The referential action.
	 */
	public ReferentialAction onDelete() default ReferentialAction.NONE;

	/**
	 * <p>
	 * Referential action to perform upon update of this key.
	 * </p>
	 *
	 * @return The referential action.
	 */
	public ReferentialAction onUpdate() default ReferentialAction.NONE;

	public String match() default "";

	public Deferrable deferrable() default Deferrable.NONE;

	/**
	 * @return The deferrable time
	 */
	public DeferrableTiming deferrableTiming() default DeferrableTiming.NONE;
}