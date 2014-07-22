package lin.wish.WebResource;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebJDItem extends WebTemplate {

	private static String baseUrl = "www.jd.com";

	public WebJDItem(String url) {
		super(url, baseUrl);
	}

	public double parsePrice() {
		Element priceEl = getDoc().select("#jd-price").get(0);
		String priceText = priceEl.text();
		if (priceText.contains("￥")) {
			priceText = priceText.substring(1);
		}
		try {
			double price = Double.parseDouble(priceText);
			return price;
		} catch (NumberFormatException e) {
			throw new IllegalStateException("不是有效的double字符串[" + priceText + "]"
					+ e.getMessage());
		}
	}

	public String parseName() {
		Element nameEl = getDoc().select("#name").get(0);
		String name = nameEl.select("h1").text();
		return name;
	}

	public String parseChuxiao() {
		Element nameEl = getDoc().select("#name").get(0);
		String chuxiao = nameEl.select("strong").text();
		return chuxiao;
	}

	public String parseId() {
		Elements ids = getDoc().select("#summary-market>div.dd>span");
		if (ids.size() == 0) {
			return "";
		}
		Element idEl = ids.get(0);
		return idEl.text();
	}

	public String parseDetail_ID() {
		Elements details = getDoc().select("#product-detail>div>ul.detail-list>li");
		Element idElement = details.get(1);
		String idString = idElement.text();
		String[] idStrings = idString.split("：");//中文的冒号
		return idStrings[1];
	}
}
