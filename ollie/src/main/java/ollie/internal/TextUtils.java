package ollie.internal;

public class TextUtils {
	public static String join(CharSequence delimiter, Object[] tokens) {
		StringBuilder sb = new StringBuilder();
		boolean firstTime = true;
		for (Object token : tokens) {
			if (firstTime) {
				firstTime = false;
			}
			else {
				sb.append(delimiter);
			}
			sb.append(token);
		}
		return sb.toString();
	}

	public static String join(CharSequence delimiter, Iterable tokens) {
		StringBuilder sb = new StringBuilder();
		boolean firstTime = true;
		for (Object token : tokens) {
			if (firstTime) {
				firstTime = false;
			}
			else {
				sb.append(delimiter);
			}
			sb.append(token);
		}
		return sb.toString();
	}

	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0)
			return true;
		else
			return false;
	}
}