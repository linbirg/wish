package lin.wish.updater;

import java.util.Iterator;
import java.util.List;

import lin.wish.BerkeleyDB.BDBPersistentQueue;
import lin.wish.WebResource.WebJDItem;
import lin.wish.dao.ProductDao;
import lin.wish.model.Product;
import lin.wish.utils.TimeHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 每个周期定时更新商品的信息。
 * 
 * */
public class Updater extends TimeTask {
	Logger logger = LoggerFactory.getLogger(Updater.class);
	private BDBPersistentQueue<String> itemQuene = null;

	private Timer updateTimer = new Timer();

	private ProductDao productDao;

	private long period = 2 * 60;

	public Updater() {
		super();
		itemQuene = new BDBPersistentQueue<String>("itemDB", "d:\\item.db");
	}

	public Thread start() throws InterruptedException {
		updateTimer.addTimerTask(this, getPeriod());
		updateTimer.start();
		// updateTimer.join();
		return updateTimer;
	}

	@Override
	protected void doTimer() {
		Iterator<String> itemsIterator = itemQuene.getIterator();
		logger.debug("Updater:itemQuene:size="
				+ String.format("%d", itemQuene.size()));
		while (itemsIterator.hasNext()) {
			String url = itemsIterator.next();
			logger.debug("Updater:itemQuene:size="
					+ String.format("%d", itemQuene.size()));
			WebJDItem jdItem = new WebJDItem(url);
			logger.debug(String.format(
					"jdItem:{name:%s,price:%f,id:%s,chuxiao:%s}", jdItem
							.parseName(), jdItem.parsePrice(),
					"".equals(jdItem.parseId()) ? jdItem.parseDetail_ID()
							: jdItem.parseId(), jdItem.parseChuxiao()));

			Product jdProduct = new Product();
			jdProduct.setName(jdItem.parseName());
			jdProduct.setCode("".equals(jdItem.parseId()) ? jdItem
					.parseDetail_ID() : jdItem.parseId());
			jdProduct.setCompany("jd");
			jdProduct.setUrl(url);
			jdProduct.setCreated_at(TimeHelper.now2Str());
			jdProduct.setUpdated_at(jdProduct.getCreated_at());
			jdProduct.setPrice(jdItem.parsePrice());
			jdProduct.setDescription(jdItem.parseChuxiao());

			List<Product> items = getProductDao().select(
					"where company = ? and code = ?", jdProduct.getCompany(),
					jdProduct.getCode());
			if (items.size() != 0) {
				jdProduct.setId(items.get(0).getId());
				jdProduct.setCreated_at(items.get(0).getCreated_at());
				getProductDao().update(jdProduct);
			} else {
				getProductDao().insert(jdProduct);
			}
		}

	}

	// {{property
	public ProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}

	public long getPeriod() {
		return period;
	}

	public void setPeriod(long period) {
		this.period = period;
	}

	// }}

}
