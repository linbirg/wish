package BerkeleyDB;

import lin.wish.BerkeleyDB.BDBPersistentQueue;

import org.junit.Assert;
import org.junit.Test;

public class PersistentQueueTest {
	
	@Test
	public void queueTest(){
		
		try(BDBPersistentQueue<String> urlUqeue = new BDBPersistentQueue<String>("d:\\wishTest.db", "wishTestDB")) {
			urlUqeue.push("test1");
		    urlUqeue.push("test2");
		    urlUqeue.push("test3");
		    urlUqeue.push("test4");
		    urlUqeue.push("test5");
		    urlUqeue.push("test6");
		    urlUqeue.push("test7");
		    urlUqeue.push("test8");
		    urlUqeue.push("test9");
		    urlUqeue.push("test10");
		    
		    String firstKey = urlUqeue.poll();
		    Assert.assertEquals("test1", firstKey);
		    firstKey = urlUqeue.poll();
		    Assert.assertEquals("test2", firstKey);
		    urlUqeue.push("test11");
		    firstKey = urlUqeue.poll();
		    Assert.assertEquals("test3", firstKey);
		}
	}
}
