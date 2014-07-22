package lin.soft.crawler;

import lin.wish.WebResource.WebTemplate;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import BerkeleyDB.BDBPersistentQueue;


/**
 * 从一个给定的网址开始，按照广度优先的顺序，抓取网页中的所有连接。
 * */
public class Crawler implements Runnable{
	Logger logger = LoggerFactory.getLogger(Crawler.class);

	private BDBPersistentQueue<String> urlQuene;
	private FilterManager filterManager = new FilterManager();
	private String baseUrl;
	//private Map<String, WebTemplate> urlMap = new HashMap<String, WebTemplate>();
	
	//private BerkeleyDBHelper helper = new BerkeleyDBHelper();

	public Crawler() {
		urlQuene = new BDBPersistentQueue<String>("wishDB","d:\\wishDb.db");
		//urlQuene.clear();
		addFilter_uniURL();
	}

	public Crawler(String startUrl) {
		this();
		baseUrl = startUrl;
		put(new UrlPair(startUrl, new WebTemplate(startUrl, baseUrl)));
	}

	public Crawler(String startUrl, WebTemplate webResource) {
		this();
		baseUrl = startUrl;
		put(new UrlPair(startUrl, webResource));
	}


	public Thread start(){
		Thread crawlerThread = new Thread(this);
		crawlerThread.start();
		return crawlerThread;
	}

	
	
	/**
	 * 增加默认过滤器，默认过滤器保证队列中url的唯一性。
	 * */
	private void addFilter_uniURL(){
		filterManager.addFilter(new UniFilter());
	}
	
	private boolean filter(String url) {
		return filterManager.filter(url);
	}
	
	private synchronized void put(UrlPair pair){
//		urlMap.put(pair.getUrl(), pair.getWeb());
//		urlQuene.offer(pair);
		urlQuene.push(pair.getUrl());
		logger.info(String.format("put:urlQuene size:%d", urlQuene.size()));
	}
	
	private synchronized UrlPair poll(){
//		UrlPair pair = urlQuene.poll();
//		urlMap.remove(pair.getUrl());
		String url = urlQuene.poll();
		logger.info(String.format("poll:urlQuene size:%d", urlQuene.size()));
		return new UrlPair(url, new WebTemplate(url, baseUrl));
	}

	public void run() {
		while (!Thread.interrupted()) {
			if (urlQuene.size() != 0) {
				UrlPair pair = poll();
				logger.debug("准备处理页面["+pair.getUrl()+"]");
				try {
					Elements links = pair.getWeb().getDoc().select("a[href]");
					for (Element link : links) {
						String url = link.attr("href");
						try {
							if (filter(url)) {
								put(new UrlPair(url,
										new WebTemplate(url, baseUrl)));
								logger.info("find url[" + url + "]");
							}
						} catch (IllegalStateException e) {
							logger.error("加载url[" + url + "]时出错，已放弃对该链接的处理。");
						}

					}
				} catch (Throwable e) {
					logger.error("抓取页面[" + pair.getUrl() + "]时出错。");
				}
			}
		}
	}

	public FilterManager getFilterManager() {
		return filterManager;
	}

	public void setFilterManager(FilterManager filterManager) {
		this.filterManager = filterManager;
	}
	
	private class UniFilter implements Filter{
		private SimpleBloomFilter bloomFilter = new SimpleBloomFilter();
		public boolean filter(String url) {
			if (bloomFilter.contains(url)) {
				//logger.debug("url["+url+"]已经存在。");
				return false;
			}else {
				bloomFilter.add(url);
				return true;
			}
		}
		
	}
}
