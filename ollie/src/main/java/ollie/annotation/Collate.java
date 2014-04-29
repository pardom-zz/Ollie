package ollie.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

@Target(FIELD)
@Retention(CLASS)
public @interface Collate {
	public enum Collation {
		BINARY("BINARY"),
		NOCASE("NOCASE"),
		RTRIM("RTRIM");

		private String mKeyword;

		Collation(String keyword) {
			mKeyword = keyword;
		}

		public String keyword() {
			return mKeyword;
		}
	}

	public Collation value();
}