package lin.wish.crawler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lin.wish.WebResource.WebJDItem;
import lin.wish.dao.ProductDao;
import lin.wish.model.Product;
import lin.wish.utils.TimeHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDITemlHandler extends URLHandler {
	Logger logger = LoggerFactory.getLogger(JDITemlHandler.class);
	
	private ProductDao productDao;

	@Override
	public boolean filter(String url) {
		String regex = "^(http://|https://)?(www)?(item.jd.com/)(.*?)(\\.html)$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			logger.debug("JDITemlHandler:url[" + url + "]");
			return true;
		}
		return false;
	}

	@Override
	protected void doHandle(String url) {
		WebJDItem jdItem = new WebJDItem(url);
		logger.debug(String.format(
				"jdItem:{name:%s,price:%f,id:%s,chuxiao:%s}",
				jdItem.parseName(),
				jdItem.parsePrice(),
				"".equals(jdItem.parseId()) ? jdItem.parseDetail_ID() : jdItem
						.parseId(), jdItem.parseChuxiao()));

		Product jdProduct = new Product();
		jdProduct.setName(jdItem.parseName());
		jdProduct.setCode("".equals(jdItem.parseId()) ? jdItem.parseDetail_ID()
				: jdItem.parseId());
		jdProduct.setCompany("jd");
		jdProduct.setUrl(url);
		jdProduct.setCreated_at(TimeHelper.now2Str());
		jdProduct.setUpdated_at(jdProduct.getCreated_at());
		jdProduct.setPrice(jdItem.parsePrice());
		jdProduct.setDescription(jdItem.parseChuxiao());
		
		productDao.insert(jdProduct);
	}

	public ProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}

}
