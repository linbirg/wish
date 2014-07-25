package lin.wish.crawler;



/**
 * URLHandler是URL的处理程序。urlhandler实现filter过滤接口。
 * 对于符合条件的url，则会处理，不符合条件的不进行处理。
 * 
 * */
public abstract class URLHandler implements Filter{

	@Override
	public boolean filter(String url){
		return true;
	}
	
	public final void handle(String url) {
		if (filter(url)) {
			doHandle(url);
		}
	}
	
	abstract protected void doHandle(String url);
}
