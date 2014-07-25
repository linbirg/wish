package lin.base.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.Types;

/**
 * 定义字段的注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
// 该注解只能用在成员变量上
public @interface Column {
	
	/**
	 * 用来存放字段的长度
	 * @return
	 */
	int length() default -1;

	/**
	 * 用来存放字段的名字
	 * 如果未指定列名，默认列名使用成员变量名
	 * @return
	 */
	String name() default "";
	
	/**
	 * 如果未指定类型，则根据属性判断
	 * @return
	 */
	int type() default Types.OTHER;
	
	
	boolean pk() default false;
}