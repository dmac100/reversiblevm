package frontend.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	/**
	 * Returns the first match in s for the pattern, or null
	 * if there is no match.
	 */
	public static String match(String s, String pattern) {
		Matcher matcher = Pattern.compile(pattern).matcher(s);
		if(matcher.find()) {
			if(matcher.groupCount() > 0) {
				return matcher.group(1);
			} else {
				return matcher.group();
			}
		} else {
			return null;
		}
	}
}
