package lin.base.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import lin.base.dao.BaseSpringDaoUtils.Transformer;
import lin.base.dao.TableDescriptor.ColumnDescriptor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;




@SuppressWarnings({ "rawtypes" })
public class BaseSpringDao<E> {

	protected JdbcTemplate jdbcTemplate;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	protected TableDescriptor tableDescriptor;

	/**
	 * Bean的类型
	 */
	protected Class clazz;

	protected RowMapper<E> rowMapper;

	public TableDescriptor getTableDescriptor() {
		return tableDescriptor;
	}

	public Class getClazz() {
		return clazz;
	}

	public String getSelectAllColumnsClause() {
		return sql_selectAllColumnsClause;
	}

	/**
	 * 这个函数是一个hack，用于判定E的真实类型。来自于
	 * 
	 * Code by Any Other Name | Reflecting generics | by Ian Robertson
	 * 
	 * 
	 * 目前这个函数不是完美解决泛型的类型判断问题，匿名类还不能使用。
	 * 
	 * @return
	 */
	private Class returnedClass() {
		clazz = this.getClass();
		
		ParameterizedType parameterizedType = null;
				
		while (clazz != Object.class) {
			try {
				parameterizedType = (ParameterizedType) clazz
						.getGenericSuperclass();
			} catch (Throwable e) {
				clazz = clazz.getSuperclass();
				continue;
			}
			break;
		}
		if (parameterizedType == null) {
			throw new IllegalStateException("无法获取类["+clazz.getName()+"]的泛型信息。");
		}
		Type type = parameterizedType.getActualTypeArguments()[0];
		return (Class) type;
	}

	public BaseSpringDao(RowMapper<E> rowMapper, TableDescriptor tableDescriptor) {
		clazz = returnedClass();
		this.tableDescriptor = tableDescriptor;

		this.rowMapper = rowMapper;

		initSQLs();

	}

	public BaseSpringDao(RowMapper<E> rowMapper) {
		clazz = returnedClass();
		tableDescriptor = BeanTableContext.findBeanTableDescriptor(clazz);
		if (tableDescriptor == null) {
			tableDescriptor = new TableDescriptor(clazz);
			// 同时被两个线程写，结果也不会有问题，所以不做synchronized控制。
			BeanTableContext.registerBeanTable(clazz, tableDescriptor);
		}

		this.rowMapper = rowMapper;

		initSQLs();

	}
	
//	@SuppressWarnings("unchecked")
//	public BaseSpringDao(Class<E> clazz) {
//		this.clazz = clazz;
//		tableDescriptor = BeanTableContext.getBeanTableDescriptor(clazz);
//		if (tableDescriptor == null) {
//			tableDescriptor = new TableDescriptor(clazz);
//			// 同时被两个线程写，结果也不会有问题，所以不做synchronized控制。
//			BeanTableContext.registerBeanTable(clazz, tableDescriptor);
//		}
//		
//		this.rowMapper = BeanPropertyRowMapper.newInstance(this.clazz);
//		initSQLs();
//	}

	protected String sql_allColumnString;

	protected String sql_pkColumnString;

	protected String sql_selectAllColumnsClause;

	private String sql_insert;

	private String sql_updateWithPK;

	private String sql_deleteWithPK;

	private int[] allColumnTypeArray;

	private int[] pkColumnTypeArray;

	private String wherePK;

	private void initSQLs() {

		sql_allColumnString = columnsToString(tableDescriptor.getColumns());

		sql_pkColumnString = columnsToString(tableDescriptor.getPkColumns());

		// 最常用的select所有列的子句
		sql_selectAllColumnsClause = String.format("select %s from %s ",
				sql_allColumnString, tableDescriptor.getTableName());

		Transformer<ColumnDescriptor, String> paramTransformer = new Transformer<ColumnDescriptor, String>() {
			//@Override
			public String transform(ColumnDescriptor e) {
				return e.getColumnName() + "=?";
			}
		};
		List<String> questionMarks = new ArrayList<String>();
		for (int i = 0; i < tableDescriptor.getColumns().size(); i++) {
			questionMarks.add("?");
		}

		sql_insert = String.format("insert into %s(%s) values(%s)",
				tableDescriptor.getTableName(), sql_allColumnString,
				BaseSpringDaoUtils.contentToString(questionMarks, ","));

		List<String> notPKColumnsParams = BaseSpringDaoUtils.transform(
				tableDescriptor.getNotPkColumns(), paramTransformer);

		String setNotPK = BaseSpringDaoUtils.contentToString(notPKColumnsParams, ", ");

		wherePK = BaseSpringDaoUtils.contentToString(BaseSpringDaoUtils.transform(
				tableDescriptor.getPkColumns(), paramTransformer), " and ");

		allColumnTypeArray = new int[tableDescriptor.getColumns().size()];

		for (int i = 0; i < tableDescriptor.getColumns().size(); i++) {
			allColumnTypeArray[i] = tableDescriptor.getColumns().get(i)
					.getType();
		}

		pkColumnTypeArray = new int[tableDescriptor.getPkColumns().size()];
		for (int i = 0; i < tableDescriptor.getPkColumns().size(); i++) {
			pkColumnTypeArray[i] = tableDescriptor.getPkColumns().get(i)
					.getType();
		}

		sql_updateWithPK = String.format("update %s set %s where %s",
				tableDescriptor.getTableName(), setNotPK, wherePK);

		sql_deleteWithPK = String.format("delete from %s where %s",
				tableDescriptor.getTableName(), wherePK);
	}

	protected String columnsToString(List<ColumnDescriptor> cs) {
		List<String> list = BaseSpringDaoUtils.transform(cs,
				new Transformer<ColumnDescriptor, String>() {
					//@Override
					public String transform(ColumnDescriptor e) {
						return e.getColumnName();
					}
				});

		return BaseSpringDaoUtils.contentToString(list, ",");
	}

	/**
	 * 
	 * @param where
	 *            包含了where之后的所有子句
	 * @param args
	 *            可以是普通的绑定参数，也可以是org.springframework.jdbc.core.
	 *            SqlParameterValue类型的绑定参数
	 * @return
	 */
	public List<E> select(String where, Object... args) {
		return jdbcTemplate.query(
				sql_selectAllColumnsClause + BaseSpringDaoUtils.killNull(where), args,
				rowMapper);
	}

	public long count(String where, Object... args) {
		return jdbcTemplate.queryForObject(String.format(
				"select count(%s) from %s %s", this.sql_pkColumnString,
				this.tableDescriptor.getTableName(), BaseSpringDaoUtils.killNull(where)),Long.class,
				args);
	}

	/**
	 * 
	 * @param where
	 *            包含了where之后的所有子句
	 * @param fromIndex
	 *            1-based index
	 * @param selectSize
	 *            size want to select
	 * @param args
	 *            可以是普通的绑定参数，也可以是org.springframework.jdbc.core.
	 *            SqlParameterValue类型的绑定参数
	 * @return
	 */
	public List<E> selectLimit(String where, int fromIndex, int selectSize,
			Object... args) {
		String originalQuerySQL = String
				.format("select %s from %s %s", sql_allColumnString,
						this.tableDescriptor.getTableName(), where);

		return selectLimit(fromIndex, selectSize, originalQuerySQL, args);
	}

	private List<E> selectLimit(long fromIndex, long selectSize,
			String originalQuerySQL, Object... args) {
		String searchQuery_rn____ = String.format(
				"select %s, rownum rn____ from (%s) ", sql_allColumnString,
				originalQuerySQL);

		// 这个SQL参考了
		// http://www.oracle.com/technetwork/issue-archive/2006/06-sep/o56asktom-086197.html
		// 以及
		// http://stackoverflow.com/questions/11680364/oracle-faster-paging-query
		String paginationSearchQuery = "select " + this.sql_allColumnString
				+ " from ( " + searchQuery_rn____
				+ " ) where rn____ >= ? and rownum <= ?";

		List<Object> argsEx = BaseSpringDaoUtils.toList(args);
		argsEx.add(fromIndex);
		argsEx.add(selectSize);

		List<E> list = this.jdbcTemplate.query(paginationSearchQuery,
				argsEx.toArray(new Object[argsEx.size()]), rowMapper);
		return list;
	}

	/**
	 * 
	 * @param where
	 * @param pageNum
	 *            1-based index
	 * @param PageSize
	 * @param args
	 * @return
	 */
	public Pair<List<E>, Long> selectPage(String where, int pageNum,
			int PageSize, Object... args) {
		String originalQuerySQL = String
				.format("select %s from %s %s", sql_allColumnString,
						this.tableDescriptor.getTableName(), where);

		return selectPage(pageNum, PageSize, originalQuerySQL, args);
	}

	/**
	 * 这个protected函数可以实现更多定制化的选择，原始SQL语句可以完整的指定。
	 * 
	 * @param pageNum
	 * @param PageSize
	 * @param originalQuerySQL
	 * @param args
	 * @return
	 */
	protected Pair<List<E>, Long> selectPage(int pageNum, int PageSize,
			String originalQuerySQL, Object... args) {
		String countQuery = String.format("select count(%s) from  (%s)",
				this.sql_pkColumnString, originalQuerySQL);

		long total = this.jdbcTemplate.queryForObject(countQuery,Long.class, args);

		List<E> list = selectLimit((pageNum - 1) * PageSize + 1, PageSize,
				originalQuerySQL, args);

		return new Pair<List<E>, Long>(list, total);
	}

	/**
	 * 因为使用了反射，所以性能较低，反射部分的代码比不使用反射性能低10倍。
	 * 但Hibernate也用反射，如果排除掉cache的作用，这个方法的性能不会比hibernate低。
	 * 
	 * 不建议使用这个函数进行批量操作
	 * 
	 * @param e
	 * @return
	 */
	public int insert(E e) {
		return jdbcTemplate.update(sql_insert,
				columnArray(e, tableDescriptor.getColumns()),
				allColumnTypeArray);
	}

	/**
	 * 因为使用了反射，所以性能较低，反射部分的代码比不使用反射性能低10倍。
	 * 但Hibernate也用反射，如果排除掉cache的作用，这个方法的性能不会比hibernate低。
	 * 
	 * 不建议使用这个函数进行批量操作
	 * 
	 * @param e
	 * @return
	 */
	public int update(E e) {
		return jdbcTemplate.update(sql_updateWithPK,
				columnArray(e, tableDescriptor.getColumns()),
				allColumnTypeArray);
	}

	public int delete(E e) {
		clazz = returnedClass();
		return jdbcTemplate.update(sql_deleteWithPK,
				columnArray(e, tableDescriptor.getPkColumns()),
				pkColumnTypeArray);
	}

	protected static <E> Object[] columnArray(E e, List<ColumnDescriptor> list) {
		Object[] objs = new Object[list.size()];

		for (int i = 0; i < objs.length; i++) {
			try {
				objs[i] = list.get(i).getReadMethod().invoke(e);
			} catch (Throwable t) {
				throw new IllegalStateException(t);
			}
		}
		return objs;
	}
}
