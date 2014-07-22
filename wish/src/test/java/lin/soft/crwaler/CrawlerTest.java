package lin.soft.crwaler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lin.soft.crawler.Crawler;
import lin.soft.crawler.Filter;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlerTest {
	Logger logger = LoggerFactory.getLogger(CrawlerTest.class);
	
	@Test
	public void crawlerTest(){
		Crawler crawler = new Crawler("http://www.jd.com");
		//只找list和item
		crawler.getFilterManager().addFilter(new Filter() {
			public boolean filter(String url) {
				String regex = "^(http://|https://)?(www)?[\\w]*?(\\.jd\\.com/).*";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(url);
				if (matcher.find()) {
					if (url.contains("en.jd.com")) {
						return false;
					}
					return true;
				}
				return false;
			}
		});
		
		crawler.start();
	}
}
