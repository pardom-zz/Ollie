package ollie.internal;

public class TypeAdapterDefinition {
	private String classPackage;
	private String className;

	private String deserializedType;
	private String serializedType;

	public TypeAdapterDefinition(String classPackage, String className, String deserializedType, String serializedType) {
		this.classPackage = classPackage;
		this.className = className;
		this.deserializedType = deserializedType;
		this.serializedType = serializedType;
	}

	public String getDeserializedType() {
		return deserializedType;
	}

	public String getSerializedType() {
		return serializedType;
	}

	public String getFqcn() {
		return classPackage + "." + className;
	}
}