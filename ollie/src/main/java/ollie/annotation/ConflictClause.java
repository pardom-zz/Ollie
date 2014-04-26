package ollie.annotation;

public enum ConflictClause {
	NONE(null),
	ROLLBACK("ROLLBACK"),
	ABORT("ABORT"),
	FAIL("FAIL"),
	IGNORE("IGNORE"),
	REPLACE("REPLACE");

	private String keyword;

	ConflictClause(String keyword) {
		this.keyword = keyword;
	}

	public String keyword() {
		return keyword;
	}
}