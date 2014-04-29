package ollie.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.CLASS;

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

	public String[] foreignColumns() default {};

	public ReferentialAction onDelete() default ReferentialAction.NONE;

	public ReferentialAction onUpdate() default ReferentialAction.NONE;

	public String match() default "";

	public Deferrable deferrable() default Deferrable.NONE;

	public DeferrableTiming deferrableTiming() default DeferrableTiming.NONE;
}