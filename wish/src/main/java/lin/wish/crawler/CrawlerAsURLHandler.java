package lin.wish.crawler;

import lin.wish.BerkeleyDB.BDBPersistentQueue;
import lin.wish.WebResource.WebTemplate;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlerAsURLHandler extends URLHandler {
	Logger logger = LoggerFactory.getLogger(CrawlerAsURLHandler.class);

	private BDBPersistentQueue<String> urlQuene;
	private FilterManager filterManager = new FilterManager();
	private String baseUrl;

	public CrawlerAsURLHandler() {
		super();
		urlQuene = new BDBPersistentQueue<String>("wishDB", "d:\\wishDb.db");
		addUniURLFilter();
	}
	
	public CrawlerAsURLHandler(String baseUrl){
		this();
		this.baseUrl = baseUrl;
	}

	// 提供给dispatcher调用的接口，crawler处理所有的url。
	@Override
	public boolean filter(String url) {
		return true;
	}

	@Override
	protected void doHandle(String url) {
		logger.debug("准备处理页面[" + url + "]");
		WebTemplate webTemplate = new WebTemplate(url, baseUrl);
		Elements links = webTemplate.getDoc().select("a[href]");
		for (Element link : links) {
			String link_url = link.attr("href");
			try {
				if (this.getFilterManager().filter(link_url)) {
					put(link_url);
					logger.debug("find url[" + link_url + "]");
				}
			} catch (IllegalStateException e) {
				logger.error("加载url[" + url + "]时出错，已放弃对该链接的处理。");
			}
		}
	}
	
	public void initQueue(String startUrl) {
		//urlQuene.clear();
		put(startUrl);
	}

	/**
	 * 增加默认过滤器，默认过滤器保证队列中url的唯一性。
	 * */
	private void addUniURLFilter() {
		getFilterManager().addFilter(new UniFilter());
	}

	private void put(String url) {
		urlQuene.push(url);
		logger.info(String.format("put:urlQuene size:%d", urlQuene.size()));
	}

	public FilterManager getFilterManager() {
		return filterManager;
	}

	public void setFilterManager(FilterManager filterManager) {
		this.filterManager = filterManager;
	}

	private class UniFilter implements Filter {
		private SimpleBloomFilter bloomFilter = new SimpleBloomFilter();

		public boolean filter(String url) {
			if (bloomFilter.contains(url)) {
				//logger.debug("url["+url+"]已访问过。");
				return false;
			} else {
				bloomFilter.add(url);
				//logger.debug("url["+url+"]未访问。");
				return true;
			}
		}

	}

}
