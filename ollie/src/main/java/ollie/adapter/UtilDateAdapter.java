package ollie.adapter;

import ollie.TypeAdapter;

import java.util.Date;

public class UtilDateAdapter extends TypeAdapter<Date, Long> {
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