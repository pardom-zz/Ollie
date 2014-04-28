package ollie.adapter;

import ollie.TypeAdapter;

import java.util.Calendar;

public class CalendarAdapter extends TypeAdapter<Calendar, Long> {
	@Override
	public Long serialize(Calendar value) {
		if (value != null) {
			return value.getTimeInMillis();
		}
		return null;
	}

	@Override
	public Calendar deserialize(Long value) {
		if (value != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(value);
			return calendar;
		}
		return null;
	}
}