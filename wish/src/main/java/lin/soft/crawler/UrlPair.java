package lin.soft.crawler;

import lin.wish.WebResource.WebTemplate;

public class UrlPair{
	private String url;
	private WebTemplate web;
	
	public UrlPair(String url,WebTemplate web){
		this.setUrl(url);
		this.setWeb(web);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public WebTemplate getWeb() {
		return web;
	}

	public void setWeb(WebTemplate web) {
		this.web = web;
	}
}
