package lin.wish.crawler;

import java.util.ArrayList;
import java.util.List;

public class FilterManager {
	private List<Filter> filters = new ArrayList<Filter>();
	
	public void addFilter(Filter filter) {
		this.filters.add(filter);
	}

	public void removeFilter(Filter filter) {
		int index = this.filters.indexOf(filter);
		if (index != -1) {
			this.filters.remove(index);
		}
	}
	
	public boolean filter(String url) {
		for (Filter filter : filters) {
			if (!filter.filter(url)) {
				return false;
			}
		}

		// 所有的过滤都通过了则返回true.
		return true;
	}
}
