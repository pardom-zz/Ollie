package ollie.annotation;

public enum ConflictClause {
	NONE(null),
	ROLLBACK("ROLLBACK"),
	ABORT("ABORT"),
	FAIL("FAIL"),
	IGNORE("IGNORE"),
	REPLACE("REPLACE");

	private String mKeyword;

	ConflictClause(String keyword) {
		mKeyword = keyword;
	}

	public String keyword() {
		return mKeyword;
	}
}