package lin.soft.wish;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class htnlunitTest {
	Logger logger = LoggerFactory.getLogger(htnlunitTest.class);
	
	@Test
	public void testCrawler() throws Exception {  
        /**HtmlUnit请求web页面*/  
        WebClient wc = new WebClient(BrowserVersion.CHROME);  
        wc.getOptions().setJavaScriptEnabled(true); //启用JS解释器，默认为true  
        wc.getOptions().setCssEnabled(false); //禁用css支持  
        wc.getOptions().setThrowExceptionOnScriptError(false); //js运行错误时，是否抛出异常  
        wc.getOptions().setTimeout(10000); //设置连接超时时间 ，这里是10S。如果为0，则无限期等待  
        HtmlPage page = wc.getPage("http://item.jd.com/531924.html");  
        String pageXml = page.asXml(); //以xml的形式获取响应文本  
  
        /**jsoup解析文档*/  
        Document doc = Jsoup.parse(pageXml, "http://www.jd.com");   
        Element price = doc.select("#jd-price").get(0);  
        logger.debug(price.text());    
        
        logger.debug(price.html()); 
        logger.debug(doc.html());
    } 
}
