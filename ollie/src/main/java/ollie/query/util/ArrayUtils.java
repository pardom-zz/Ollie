package ollie.query.util;

public class ArrayUtils {
	public static String[] addAll(final String[] array1, final String... array2) {
		if (array1 == null) {
			return clone(array2);
		} else if (array2 == null) {
			return clone(array1);
		}
		final String[] joinedArray = new String[array1.length + array2.length];
		System.arraycopy(array1, 0, joinedArray, 0, array1.length);
		System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
		return joinedArray;
	}

	public static String[] clone(final String[] array) {
		if (array == null) {
			return null;
		}
		return array.clone();
	}
}
