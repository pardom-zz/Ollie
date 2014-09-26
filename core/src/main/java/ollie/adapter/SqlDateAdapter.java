package ollie.adapter;

import ollie.TypeAdapter;

import java.sql.Date;

public class SqlDateAdapter extends TypeAdapter<Date, Long> {
	@Override
	public Long serialize(Date value) {
		if (value != null) {
			return value.getTime();
		}
		return null;
	}

	@Override
	public Date deserialize(Long value) {
		if (value != null) {
			return new Date(value);
		}
		return null;
	}
}