package lin.wish.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeHelper {
	
	public static final String DEFAULT_DATE_TIME_FORMAT = "yyyyMMdd HH:mm:ss"; 
	
	public static String now2Str() {
		SimpleDateFormat smfDateFormat = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
		Date nowDate = new Date(System.currentTimeMillis());
		return smfDateFormat.format(nowDate);
	}
}
