package lin.soft.wbresource;

import lin.wish.WebResource.WebJDItem;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebJdTest {
	private Logger logger = LoggerFactory.getLogger(WebJdTest.class);

	@Test
	public void testWebJd() {
		WebJDItem jdItem = new WebJDItem("http://item.jd.com/531924.html");
		logger.debug(String.format(
				"jdItem:{name:%s,price:%f,id:%s,chuxiao:%s}",
				jdItem.parseName(),
				jdItem.parsePrice(),
				"".equals(jdItem.parseId()) ? jdItem.parseDetail_ID() : jdItem
						.parseId(), jdItem.parseChuxiao()));
		//logger.debug(jdItem.getDoc().html());
	}
}
