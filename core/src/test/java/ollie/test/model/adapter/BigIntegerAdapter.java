package ollie.test.model.adapter;

import ollie.TypeAdapter;

import java.math.BigInteger;

public class BigIntegerAdapter implements TypeAdapter<BigInteger, String> {
	@Override
	public String serialize(BigInteger value) {
		return value.toString();
	}

	@Override
	public BigInteger deserialize(String value) {
		return new BigInteger(value);
	}
}
