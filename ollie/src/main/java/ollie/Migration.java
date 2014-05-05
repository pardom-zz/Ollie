package ollie;

@ollie.annotation.Migration
public abstract class Migration implements Comparable<Migration> {
	public abstract int getVersion();

	public abstract String[] getStatements();

	@Override
	public int compareTo(Migration migration) {
		return getVersion() < migration.getVersion() ? -1 : (getVersion() == migration.getVersion() ? 0 : 1);
	}
}
