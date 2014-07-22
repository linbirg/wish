package lin.soft.crwaler;

import lin.soft.crawler.SimpleBloomFilter;

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
}
