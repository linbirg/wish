package BerkeleyDB;

import java.util.Iterator;

import lin.wish.BerkeleyDB.BDBPersistentQueue;

import org.junit.Assert;
import org.junit.Test;

public class PersistentQueueTest {
	
	@Test
	public void queueTest(){
		
		try(BDBPersistentQueue<String> urlUqeue = new BDBPersistentQueue<String>("d:\\wishTest.db", "wishTestDB")) {
			urlUqeue.clear();
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
	
	@Test
	public void test_get_next(){
		try(BDBPersistentQueue<String> items = new BDBPersistentQueue<>("test", "d:\\testDB")){
			items.clear();
			items.push("test1");
			items.push("test2");
			items.push("test3");
			items.push("test4");
			items.push("test5");
			items.push("test6");
			
			Iterator<String> itemsIterator = items.getIterator();
			Assert.assertEquals(itemsIterator.next(), "test1");
			Assert.assertEquals(itemsIterator.next(), "test2");
			Assert.assertEquals(itemsIterator.next(), "test3");
			Assert.assertEquals(itemsIterator.next(), "test4");
			Assert.assertEquals(itemsIterator.next(), "test5");
			Assert.assertEquals(itemsIterator.next(), "test6");
			Assert.assertEquals(itemsIterator.next(), null);
		}
	}
}
