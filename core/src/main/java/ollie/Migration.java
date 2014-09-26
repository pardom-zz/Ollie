package ollie;

@ollie.annotation.Migration
public abstract class Migration implements Comparable<Migration> {
	public abstract int getVersion();

	public abstract String[] getStatements();

	@Override
	public int compareTo(Migration migration) {
		return Integer.compare(getVersion(), migration.getVersion());
	}
}
