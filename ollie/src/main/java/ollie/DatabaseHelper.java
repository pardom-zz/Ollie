package ollie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.util.List;

public final class DatabaseHelper extends SQLiteOpenHelper {
	public DatabaseHelper(Context context, String name, int version) {
		super(context, name, null, version);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		executePragmas(db);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		executePragmas(db);
		executeCreate(db);
		executeMigrations(db, -1, db.getVersion());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		executePragmas(db);
		executeCreate(db);
		executeMigrations(db, oldVersion, newVersion);
	}

	private void executePragmas(SQLiteDatabase db) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
	}

	private void executeCreate(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			for (String tableDefinition : Ollie.getTableDefinitions()) {
				db.execSQL(tableDefinition);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	private boolean executeMigrations(SQLiteDatabase db, int oldVersion, int newVersion) {
		boolean migrationExecuted = false;
		final List<? extends Migration> migrations = Ollie.getMigrations();

		db.beginTransaction();
		try {
			for (Migration migration : migrations) {
				if (migration.getVersion() > oldVersion && migration.getVersion() <= newVersion) {
					for (String statement : migration.getStatements()) {
						db.execSQL(statement);
					}
					migrationExecuted = true;
				}
			}
		} finally {
			db.setTransactionSuccessful();
		}
		db.endTransaction();

		return migrationExecuted;
	}
}
