package lin.soft.crwaler;

import lin.wish.crawler.SimpleBloomFilter;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BloomFilterTest {
	private Logger logger = LoggerFactory.getLogger(BloomFilterTest.class);
	
	@Test
	public void bloomFilterTest(){
		SimpleBloomFilter bloomFilter = new SimpleBloomFilter();
		bloomFilter.add("linbirg");
		logger.debug(bloomFilter.contains("linbirg")?"yes":"no");
	}
	
	@Test
	public void bloomTest_rate(){
		SimpleBloomFilter bloomFilter = new SimpleBloomFilter();
		bloomFilter.add("http://mall.jd.com/view_search-256271-1609824-1-0-20-1.html");
		logger.debug(bloomFilter.contains("http://mall.jd.com/view_search-256271-1609824-1-0-20-1.html")?"yes":"no");
	}
}
