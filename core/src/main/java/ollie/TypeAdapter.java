package ollie;

@ollie.annotation.TypeAdapter
public interface TypeAdapter<D, S> {
	public S serialize(D value);

	public D deserialize(S value);
}