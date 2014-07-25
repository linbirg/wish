package lin.base.dao;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/** 
 * 这个注解类的代码来自网络。这些注释都是作者的学习笔记。
 * 
 * 注解的分类
 * 1.标记注解(marker annotation)
 * 注解体内没有定义任何元素，只起一个标记提示作用
 * 常见的就是java.lang包下的Deprecated，Override，SuppressWarnings
   Deprecated 编译时会提示方法过时
   Override 编译时验证重写父类方法签名是否正确
   SuppressWarnings 取消警告
   2.元注解
   只用来修饰注解定义的注解
   下面用到的Retention,Target
   Retention用来指定定义的注解要保留到什么时候
   有三个枚举值：
   RetentionPolicy.SOURCE 编译是会调用，不会保留到class文件中
   RetentionPolicy.CLASS  会跟随保留到class文件中
   RetentionPolicy.RUNTIME 保留到class文件中，并且class被加载时还可以通过反射操作注解
   
   Target用来规定注解可以修饰的程序元素的种类
   其有一个ElementType[]的枚举数组参数
    ElementType.PACKAGE 包 
	ElementType.TYPE 类，接口，注解，枚举
	ElementType.METHOD 方法声明
	ElementType.FIELD  字段
	......
 * 注解一旦定义好之后，就可以像使用public,static这样的的modifiers一样，用注解修饰类，方法或属性
 */
@Retention(RetentionPolicy.RUNTIME)//可以保留到类被加载运行时
@Target(ElementType.TYPE) //指定该注解用来修饰类...
public @interface Table { //定义注解的关键字@interface
	
	/**
	 * 元素定义的返回类型限定为：基本类型,String,Class,emum,annotation
		或者是前述类型的数组
	 */
	String name();
	
}