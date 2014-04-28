package ollie;

import android.database.Cursor;
import android.provider.BaseColumns;
import ollie.annotation.AutoIncrement;
import ollie.annotation.Column;
import ollie.annotation.PrimaryKey;

import java.util.List;

public abstract class Model {
	@Column(BaseColumns._ID)
	@PrimaryKey
	@AutoIncrement
	public Long id;

	public static final <T extends Model> T find(Class<T> cls, Long id) {
		List<T> result = Ollie.query(cls, false, null, BaseColumns._ID + "=?", new String[]{id.toString()}, null, null, null, "1");
		if (result.size() > 0) {
			return result.get(0);
		}
		return null;
	}

	public final void load(Cursor cursor) {
		Ollie.load(this, cursor);
		Ollie.putEntity(this);
	}

	public final Long save() {
		id = Ollie.save(this);
		Ollie.putEntity(this);
		notifyChange();
		return id;
	}

	public final void delete() {
		Ollie.delete(this);
		Ollie.removeEntity(this);
		notifyChange();
		id = null;
	}

	private void notifyChange() {
		if (OllieProvider.isImplemented()) {
			Ollie.getContext().getContentResolver().notifyChange(OllieProvider.createUri(getClass(), id), null);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Model && id != null) {
			final Model other = (Model) obj;
			return Ollie.getTableName(getClass()).equals(Ollie.getTableName(other.getClass())) && id.equals(other.id);
		}
		return this == obj;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 17 + getClass().getName().hashCode();
		hash = hash * 31 + (id != null ? id.intValue() : super.hashCode());
		return hash;
	}
}