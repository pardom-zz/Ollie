package ollie;

import android.database.Cursor;
import android.provider.BaseColumns;
import ollie.annotation.AutoIncrementing;
import ollie.annotation.Column;
import ollie.annotation.PrimaryKey;

import java.util.List;

public abstract class Model {
	@Column(BaseColumns._ID)
	@PrimaryKey
	@AutoIncrementing
	public Long id;

	public static final <T extends Model> T find(Class<T> cls, Long id) {
		List<T> result = Ollie.query(cls, false, null, BaseColumns._ID + "=?", new String[]{id.toString()}, null, null, null, null);
		if (result.size() > 0) {
			return result.get(0);
		}
		return null;
	}

	public final void load(Cursor cursor) {
		beforeLoad();
		Ollie.load(this, cursor);
		Ollie.putEntity(this);
		afterLoad();
	}

	public final Long save() {
		beforeSave();
		id = Ollie.save(this);
		Ollie.putEntity(this);
		afterSave();
		return id;
	}

	protected void beforeLoad() {
	}

	protected void afterLoad() {
	}

	protected void beforeSave() {
	}

	protected void afterSave() {
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 17 + getClass().getName().hashCode();
		hash = hash * 31 + (id != null ? id.intValue() : super.hashCode());
		return hash;
	}
}