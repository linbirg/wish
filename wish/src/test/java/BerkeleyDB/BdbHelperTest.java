package BerkeleyDB;

import org.junit.Assert;
import org.junit.Test;

public class BdbHelperTest {
	//private Logger logger = LoggerFactory.getLogger(BdbHelperTest.class);
	
	@Test
	public void helperTest(){
		BerkeleyDBHelper helper = new BerkeleyDBHelper();
		helper.open("testDb", "e:\\testdb");
		
	    helper.insert("test1", "test1");
	    helper.insert("test2", "test2");
	    helper.insert("test3", "test3");
	    helper.insert("test4", "test3");
	    helper.insert("test5", "test3");
	    helper.insert("test6", "test3");
	    helper.insert("test7", "test3");
	    helper.insert("test8", "test3");
	    helper.insert("test9", "test3");
	    helper.insert("test10", "test3");
	    
	    
	    String firstKey = helper.getFirst();
	    Assert.assertEquals("test1", firstKey);
	    String lastKey = helper.getLast();
	    //Assert.assertEquals("test10", lastKey);
	    helper.delete("test10");
	    lastKey = helper.getLast();
	    Assert.assertEquals("test9", lastKey);
	    
	    helper.delete("test1");
	    firstKey = helper.getFirst();
	    Assert.assertEquals("test2", lastKey);
	}
}
