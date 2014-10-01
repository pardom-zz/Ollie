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
 * An annotation that indicates a member should define its SQLite column using the COLLATE constraint. Must be
 * used in conjunction with {@link ollie.annotation.Column}.
 * </p>
 * <p>
 * <a href="http://www.sqlite.org/datatype3.html#collation">http://www.sqlite.org/datatype3.html#collation</a>
 * <a href="http://www.sqlite.org/syntaxdiagrams.html#column-constraint">
 * http://www.sqlite.org/syntaxdiagrams.html#column-constraint
 * </a>
 * </p>
 */
@Target(FIELD)
@Retention(CLASS)
public @interface Collate {
	/**
	 * When SQLite compares two strings, it uses a collating sequence or collating function (two words for the same
	 * thing) to determine which string is greater or if the two strings are equal. SQLite has three built-in
	 * collating functions: BINARY, NOCASE, and RTRIM.
	 */
	public enum CollatingSequence {
		/**
		 * Compares string data using memcmp(), regardless of text encoding.
		 */
		BINARY("BINARY"),
		/**
		 * The same as binary, except the 26 upper case characters of ASCII are folded to their lower case equivalents
		 * before the comparison is performed. Note that only ASCII characters are case folded. SQLite does not
		 * attempt to do full UTF case folding due to the size of the tables required.
		 */
		NOCASE("NOCASE"),
		/**
		 * The same as binary, except that trailing space characters are ignored.
		 */
		RTRIM("RTRIM");

		private String mKeyword;

		CollatingSequence(String keyword) {
			mKeyword = keyword;
		}

		public String keyword() {
			return mKeyword;
		}
	}

	/**
	 * Returns a collating sequence to use for string comparison.
	 *
	 * @return The collating sequence.
	 */
	public CollatingSequence value();
}