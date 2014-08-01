package lin.wish.updater;

import lin.wish.dao.ProductDao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UpdaterTest {
	Logger logger = LoggerFactory.getLogger(UpdaterTest.class);

	ProductDao productDao;

	@Before
	public void before() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"/application-context.xml");

		Assert.assertNotNull(applicationContext);
		productDao = (ProductDao) applicationContext.getBean("productDao");
		Assert.assertNotNull(productDao);
	}
	
	@Test
	public void test_updater() throws InterruptedException{
		Updater updater = new Updater();
		updater.setProductDao(productDao);
		updater.setPeriod(30);
		updater.start();
		Thread.currentThread().join();
	}

}
