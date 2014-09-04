package lin.layoutsample.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	public static String replace(String target, String regex,String replacement) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(target);
		String result = "";
		if (matcher.find()) {
			result = matcher.replaceAll(Matcher.quoteReplacement(replacement));
		}
		return result;
	}
}
