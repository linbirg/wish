package lin.soft.crwaler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lin.wish.crawler.CrawlerAsURLHandler;
import lin.wish.crawler.Filter;
import lin.wish.crawler.JDITemlHandler;
import lin.wish.crawler.URLDispatcher;
import lin.wish.crawler.URLHandler;
import lin.wish.dao.ProductDao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DispatcherTest {
	Logger logger = LoggerFactory.getLogger(DispatcherTest.class);
	ProductDao productDao;
	
	@Before
	public void testAppication(){
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"/application-context.xml");
		
		Assert.assertNotNull(applicationContext);
		productDao = (ProductDao)applicationContext.getBean("productDao");
		Assert.assertNotNull(productDao);
	}
	
	@Test
	public void testDispatcherRemoveHandler() throws InterruptedException{
		URLDispatcher spider = new URLDispatcher();
		JDITemlHandler jdiTemlHandler = new JDITemlHandler();
		
		spider.addHandler(jdiTemlHandler);
		logger.debug(String.format("count:%d", spider.handlerCount()));
		Thread dispatchThread = spider.start();
		dispatchThread.join(100000);
		
		spider.removeHandler(jdiTemlHandler);
		logger.debug(String.format("count:%d", spider.handlerCount()));
		dispatchThread.join(100000);
		
		spider.addHandler(jdiTemlHandler);
		logger.debug(String.format("count:%d", spider.handlerCount()));
		dispatchThread.join();
	}
	
	@Test
	public void testHandlerList(){
		List<URLHandler> handlers = new ArrayList<URLHandler>();
		
		URLHandler jdItemHandler = new JDITemlHandler();
		handlers.add(jdItemHandler);
		Assert.assertEquals(handlers.size(), 1);
		
		handlers.remove(jdItemHandler);
		Assert.assertEquals(handlers.size(), 0);
	}
	
	@Test
	public void test_dispatcher_with_crawler() throws InterruptedException{
		URLDispatcher dispatcher = new URLDispatcher();
		JDITemlHandler jdiTemlHandler = new JDITemlHandler();
		jdiTemlHandler.setProductDao(productDao);
		
		CrawlerAsURLHandler crawlerHandler = new CrawlerAsURLHandler("http://www.jd.com");
		crawlerHandler.getFilterManager().addFilter(new Filter() {
			
			@Override
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
		
		//crawlerHandler.initQueue("http://www.jd.com");
		
		dispatcher.addHandler(jdiTemlHandler);
		dispatcher.addHandler(crawlerHandler);
		
		Thread dispatcherThread = dispatcher.start();
		dispatcherThread.join();
	}
}
