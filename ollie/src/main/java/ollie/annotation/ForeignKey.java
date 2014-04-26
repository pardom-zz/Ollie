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

		private String keyword;

		ReferentialAction(String keyword) {
			this.keyword = keyword;
		}

		public String keyword() {
			return keyword;
		}
	}

	public enum Deferrable {
		NONE(null),
		DEFERRABLE("DEFERRABLE"),
		NOT_DEFERRABLE("NOT DEFERRABLE");

		private String keyword;

		Deferrable(String keyword) {
			this.keyword = keyword;
		}

		public String keyword() {
			return keyword;
		}
	}

	public enum DeferrableTiming {
		NONE(null),
		DEFERRED("INITIALLY DEFERRED"),
		IMMEDIATE("INITIALLY IMMEDIATE");

		private String keyword;

		DeferrableTiming(String keyword) {
			this.keyword = keyword;
		}

		public String keyword() {
			return keyword;
		}

	}

	public String[] foreignColumns() default {};

	public ReferentialAction onDelete() default ReferentialAction.NONE;

	public ReferentialAction onUpdate() default ReferentialAction.NONE;

	public String match() default "";

	public Deferrable deferrable() default Deferrable.NONE;

	public DeferrableTiming deferrableTiming() default DeferrableTiming.NONE;
}