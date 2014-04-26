package ollie.internal;

public class TypeAdapterDefinition {
	private String classPackage;
	private String className;
	private String deserializedType;
	private String serializedType;

	public void setClassPackage(String classPackage) {
		this.classPackage = classPackage;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getDeserializedType() {
		return deserializedType;
	}

	public void setDeserializedType(String deserializedType) {
		this.deserializedType = deserializedType;
	}

	public String getSerializedType() {
		return serializedType;
	}

	public void setSerializedType(String serializedType) {
		this.serializedType = serializedType;
	}

	public String getFqcn() {
		return classPackage + "." + className;
	}
}