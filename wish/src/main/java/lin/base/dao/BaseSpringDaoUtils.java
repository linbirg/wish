package lin.base.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class BaseSpringDaoUtils {
	//{{ public static
	/**
	 * 转换器。配合Utils.transform函数，对列表内容进行变换。
	 * 
	 * @param <E>
	 *            原对象
	 * @param <T>
	 *            转换后的对象
	 */
	public static interface Transformer<E, T> {
		public T transform(E e);
	}

	/**
	 * 对List的内容进行变换
	 * 
	 * @param elist
	 *            包含类型为E的对象的列表
	 * @param transformer
	 *            转换器
	 * @return 包含类型为T的对象的列表
	 */
	public static <E, T> List<T> transform(Collection<E> elist,
			Transformer<E, T> transformer) {
		List<T> tlist = new ArrayList<T>();
		for (E e : elist) {
			tlist.add(transformer.transform(e));
		}
		return tlist;
	}

	public static String contentToString(List<String> list, String separator) {
		if (list == null)
			return "";
		if (list.size() == 0)
			return "";
		if (list.size() == 1)
			return list.get(0);
		StringBuilder sb = new StringBuilder();
		sb.append(list.get(0));
		for (int i = 1; i < list.size(); i++) {
			sb.append(separator);
			sb.append(list.get(i));
		}
		return sb.toString();
	}
	
	
	public static String killNull(String s) {
		if (s == null)
			return "";
		else
			return s;
	}
	
	public static <T> List<T> toList(T[] array) {
		if (array == null)
			return Collections.emptyList();
		List<T> list = new ArrayList<T>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}
	//}}
}
