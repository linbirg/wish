package lin.base.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;



public class BeanTableContext {

	/**
	 * 这个对象本来打算放在BaseSpringDao中，而且也不是Map的形式，每个泛型子类自己管理自己的TableDescriptor就可以了。
	 * 
	 * 这是一个hack，因为Java的泛型中，静态变量会被各个继承或实现的子类共用的，因此，无法分别对每个子类保存自己的TableDescriptor。
	 * 为了实现这个功能，只好用共用的静态变量写一个TableDescriptor的注册表。
	 */
	private static final Map<Class<?>, TableDescriptor> beanTableRegistry = new HashMap<Class<?>, TableDescriptor>();

	public static TableDescriptor findBeanTableDescriptor(Class<?> clazz) {
		if(clazz == null)
			return null;
		
		TableDescriptor td = beanTableRegistry.get(clazz);
		while(td == null)
		{
			clazz = clazz.getSuperclass();
			if(clazz == Object.class || clazz == null)
				break;
			td = beanTableRegistry.get(clazz);
		}
		return td;
	}

	public static void registerBeanTable(Class<?> clazz, TableDescriptor td) {
		Collections.synchronizedMap(beanTableRegistry).put(clazz, td);
	}

}