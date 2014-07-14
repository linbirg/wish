package lin.soft.wish;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSoupTest {
	Logger logger = LoggerFactory.getLogger(JSoupTest.class);
	
	@Test
	public void JSoupSample() throws IOException{
		Document doc = Jsoup.connect("http://item.jd.com/531922.html").get();
		logger.debug("title:" + doc.title());
		//logger.debug(doc.html());
		Element el=doc.select("div[class=dd] span").first();
		logger.debug(el.text());//这就是编号。
	
		logger.debug(el.toString());
	}
}
