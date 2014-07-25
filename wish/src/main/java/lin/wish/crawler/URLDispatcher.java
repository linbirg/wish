package lin.wish.crawler;

import java.util.ArrayList;
import java.util.List;

import lin.wish.BerkeleyDB.BDBPersistentQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 从网址队列中，选取符合需求的网址，根据规则提取数据。
 * */
public class URLDispatcher implements Runnable{
	private Logger logger = LoggerFactory.getLogger(URLDispatcher.class);
	
	
	private BDBPersistentQueue<String> urlQueue;
	private List<URLHandler> handlers;
	
	public URLDispatcher(){
		urlQueue = new BDBPersistentQueue<String>("wishDB","d:\\wishDb.db");
		handlers = new ArrayList<URLHandler>();
	}

	public void addHandler(URLHandler handler) {
		synchronized (handler) {
			handlers.add(handler);
		}
	}
	
	public void removeHandler(URLHandler handler) {
		synchronized (handler) {
			handlers.remove(handler);
		}
	}
	
	public int handlerCount() {
		return handlers.size();
	}

	public void run() {
		while (!Thread.interrupted()) {
			try {
				String url = poll();
				for (URLHandler handler : handlers) {
					handler.handle(url);
				}
			} catch (Throwable e) {
				logger.debug("URLDispatcher出现异常。",e);
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				logger.debug("URLDispatcher InterruptedException");
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
		logger.debug(String.format("URLDispatcher:poll:url["+url+"] size:%d", urlQueue.size()));
		return url;
	}
}
