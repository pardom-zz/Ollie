package ollie;

@ollie.annotation.TypeAdapter
public abstract class TypeAdapter<D, S> {
	public abstract S serialize(D value);

	public abstract D deserialize(S value);
}