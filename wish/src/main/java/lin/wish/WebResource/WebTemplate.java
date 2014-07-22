package lin.wish.WebResource;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class WebTemplate {

	Logger logger = LoggerFactory.getLogger(WebTemplate.class);

	private WebClient wc = null;
	private String url;
	private String baseUrl;
	private Document doc = null;

	public WebTemplate(String url, String baseUrl) {
		this.setUrl(url);
		this.setBaseUrl(baseUrl);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Document getDoc() {
		return fetchDocument();
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}
	
	public Document fetchDocument(){
		if (doc == null) {
			parse();
		}
		
		return doc;
	}

	public void parse() {
		/** HtmlUnit请求web页面 */
		if (wc == null) {
			this.wc = new WebClient(BrowserVersion.CHROME);
		}
		wc.getOptions().setJavaScriptEnabled(true); // 启用JS解释器，默认为true
		wc.getOptions().setCssEnabled(false); // 禁用css支持
		wc.getOptions().setThrowExceptionOnScriptError(false); // js运行错误时，是否抛出异常
		wc.getOptions().setTimeout(10000); // 设置连接超时时间 ，这里是10S。如果为0，则无限期等待

		HtmlPage page;
		try {
			page = wc.getPage(url);
			String pageXml = page.asXml(); // 以xml的形式获取响应文本
			/** jsoup解析文档 */
			this.setDoc(Jsoup.parse(pageXml, baseUrl));
		} catch (FailingHttpStatusCodeException e) {
			// logger.error("获取页面["+url+"]时出错.",e);
			throw new IllegalStateException("获取页面[" + url + "]时出错."
					+ e.getMessage());
		} catch (MalformedURLException e) {
			throw new IllegalStateException("获取页面[" + url + "]时出错，非法的地址。"
					+ e.getMessage());
		} catch (IOException e) {
			// logger.error("获取页面["+url+"]时出错.",e);
			throw new IllegalStateException("获取页面[" + url + "]时IO异常出错."
					+ e.getMessage());
		}
	}

}
