package ollie.test.model.migration;

import ollie.Migration;

public class AddDateColumnMigration extends Migration {
	@Override
	public int getVersion() {
		return 2;
	}

	@Override
	public String[] getStatements() {
		return new String[]{
				"ALTER TABLE notes ADD COLUMN date INTEGER"
		};
	}
}
