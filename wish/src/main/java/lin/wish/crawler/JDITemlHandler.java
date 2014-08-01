package lin.wish.crawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lin.wish.BerkeleyDB.BDBPersistentQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDITemlHandler extends URLHandler {
	Logger logger = LoggerFactory.getLogger(JDITemlHandler.class);
	private BDBPersistentQueue<String> itemQuene = null;
	
	public JDITemlHandler() {
		itemQuene = new BDBPersistentQueue<String>("itemDB", "d:\\item.db");
	}
	

	@Override
	public boolean filter(String url) {
		String regex = "^(http://|https://)?(www)?(item.jd.com/)(.*?)(\\.html)$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			logger.debug("JDITemlHandler:url[" + url + "]");
			return true;
		}
		return false;
	}

	@Override
	protected void doHandle(String url) {
		itemQuene.push(url);
		logger.debug("JDItem:url["+url+"] count:" + String.format("%d", itemQuene.size()));
	}
}
