package lin.soft.crawler;

import lin.wish.WebResource.WebJDItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import BerkeleyDB.BDBPersistentQueue;


/**
 * 从网址队列中，选取符合需求的网址，根据规则提取数据。
 * */
public class Spider implements Runnable{
	private Logger logger = LoggerFactory.getLogger(Spider.class);
	
	private FilterManager filterManager = new FilterManager();
	private BDBPersistentQueue<String> urlQueue;
	
	public Spider(){
		urlQueue = new BDBPersistentQueue<String>("wishDB","d:\\wishDb.db");
	}

	public FilterManager getFilterManager() {
		return filterManager;
	}

	public void setFilterManager(FilterManager filterManager) {
		this.filterManager = filterManager;
	}

	public void run() {
		while (!Thread.interrupted()) {
			try {
				String url = poll();
				if (filterManager.filter(url)) {
					WebJDItem jdItem = new WebJDItem(url);
					logger.debug(String.format(
							"jdItem:{name:%s,price:%f,id:%s,chuxiao:%s}",
							jdItem.parseName(),
							jdItem.parsePrice(),
							"".equals(jdItem.parseId()) ? jdItem.parseDetail_ID() : jdItem
									.parseId(), jdItem.parseChuxiao()));
				}
			} catch (Throwable e) {
				logger.debug("spdier出现异常。",e);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.debug("spider InterruptedException");
			}
		}
	}
	
	public Thread start() {
		Thread spiderThread = new Thread(this);
		spiderThread.start();
		return spiderThread;
	}
	
	private String poll(){
		String url = urlQueue.poll();
		logger.debug(String.format("spider:poll:url["+url+"] size:%d", urlQueue.size()));
		return url;
	}
}
