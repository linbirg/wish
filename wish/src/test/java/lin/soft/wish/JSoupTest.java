package lin.soft.wish;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSoupTest {
	Logger logger = LoggerFactory.getLogger(JSoupTest.class);
	
	//@Test
	public void JSoupSample() throws IOException{
		Document doc = Jsoup.connect("http://item.jd.com/531922.html").get();
		logger.debug("title:" + doc.title());
		//logger.debug(doc.html());
		Element el=doc.select("div[class=dd] span").first();
		logger.debug(el.text());//这就是编号。
	
		logger.debug(el.toString());
	}
	
	@Test
	public void getWeb(){
		String uriString = "http://item.jd.com/888981.html";
		StringBuilder urlStringBuilder = new StringBuilder(uriString);
		
		// 利用URL生成一个HttpGet请求
		HttpGet httpGet = new HttpGet(urlStringBuilder.toString());
		httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;"); 
		httpGet.setHeader("Accept-Language", "zh-cn"); 
		httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3"); 
		httpGet.setHeader("Accept-Charset", "utf-8"); 
		httpGet.setHeader("Keep-Alive", "300"); 
		httpGet.setHeader("Connection", "Keep-Alive"); 
		httpGet.setHeader("Cache-Control", "no-cache"); 
		//httpGet.setHeader("Referer",uriString);
		String webString = getWebBy(httpGet);
		logger.debug(webString);
		
		Document doc = Jsoup.parse(webString);
		logger.debug("title:" + doc.title());
		//logger.debug(doc.html());
		Element el=doc.select("#jd-price").first();
		logger.debug(el.text());//这就是编号。
	
		logger.debug(el.toString());
	}
	
	private String getWebBy(HttpUriRequest httpMethod) {
		String uriString = httpMethod.getURI().toString();
		HttpClient httpClient = HttpClients.createDefault();
		HttpResponse httpResponse = null;
		StringBuilder entityStringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		//JSONObject resultJsonObject = null;
		
		try {
			// HttpClient发出一个HttpGet请求
			httpResponse = httpClient.execute(httpMethod);
		} catch (IOException e) {
			logger.error("地址[" + uriString + "]无法访问", e);
			throw new IllegalStateException("地址[" + uriString + "]无法访问");
		}

		// 得到httpResponse的状态响应码
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		if (statusCode == HttpStatus.SC_OK) {
			// 得到httpResponse的实体数据
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				try {
					bufferedReader = new BufferedReader(new InputStreamReader(
							httpEntity.getContent(), "UTF-8"), 8 * 1024);
					String line = null;
					while ((line = bufferedReader.readLine()) != null) {
						entityStringBuilder.append(line + "/n");
					}
					
					// 利用从HttpEntity中得到的String生成JsonObject
					return entityStringBuilder.toString();
				} catch (UnsupportedEncodingException e) {
					String errMsg = "从地址[" + uriString + "]获得的字符串不是有效的UTF-8格式";
					logger.error(errMsg, e);
					throw new IllegalStateException(errMsg);
				} catch (IOException e) {
					logger.error("无法buffer中读取数据", e);
					throw new IllegalStateException("无法buffer中读取数据");
				}
			}
		}else {
			logger.error("403:page not found["+httpMethod.getURI().toString()+"]");
		}

		return null;
	}
}
