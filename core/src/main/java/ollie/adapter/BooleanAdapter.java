package ollie.adapter;

import ollie.TypeAdapter;

public class BooleanAdapter extends TypeAdapter<Boolean, Integer> {
	@Override
	public Integer serialize(Boolean value) {
		return value ? 1 : 0;
	}

	@Override
	public Boolean deserialize(Integer value) {
		return value != 0;
	}
}