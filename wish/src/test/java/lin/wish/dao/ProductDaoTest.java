package lin.wish.dao;

import lin.wish.model.Product;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;



public class ProductDaoTest {
	Logger logger = LoggerFactory.getLogger(ProductDao.class);
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
	public void test_product_dao(){
		Assert.assertNotNull(productDao);
		long count = productDao.count("");
		Product jdItem = new Product();
		productDao.insert(jdItem);
		long count_new = productDao.count("");
		Assert.assertEquals(count_new, count+1);
		productDao.delete(jdItem);
		count_new = productDao.count("");
		Assert.assertEquals(count_new, count);
	}
}
