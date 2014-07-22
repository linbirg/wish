package lin.soft.crwaler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lin.soft.crawler.Crawler;
import lin.soft.crawler.Filter;
import lin.soft.crawler.Spider;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpiderAndCrawlerTest {
	Logger logger = LoggerFactory.getLogger(SpiderAndCrawlerTest.class);
	
	@Test
	public void unionTest() throws InterruptedException{
		Thread crawlerThread = startCrawler();
		Thread spiderThread = startSpider();
		
		crawlerThread.join();
		spiderThread.join();
	}
	
	private Thread startCrawler(){
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
		
	  return crawler.start();
	}
	
	private Thread startSpider(){
		Spider spider = new Spider();
		spider.getFilterManager().addFilter(new Filter() {
			
			public boolean filter(String url) {
				logger.debug("spider:filter:"+url);
				String regex = "^(http://|https://)?(www)?(item.jd.com/)(.*?)(\\.html)$";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(url);
				if (matcher.find()) {
					logger.debug("spider:filter:true");
					return true;
				}
				return false;
			}
		});
		return spider.start();
	}
	
	@Test
	public void testSpider() throws InterruptedException {
		Thread spiderThread = startSpider();
		spiderThread.join();
	}
}
