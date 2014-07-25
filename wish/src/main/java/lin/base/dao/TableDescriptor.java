package lin.base.dao;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;


/**
 * 使用Bean的Annotations来描述数据库表，用于DAO层。
 * 
 * 
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class TableDescriptor {

	public static class ColumnDescriptor {
		private Field field;

		private String columnName;

		private int length;

		private int type;

		private boolean pk;

		private PropertyDescriptor pd;

		private Method writeMethod;

		private Method readMethod;

		public boolean isPk() {
			return pk;
		}

		public Field getField() {
			return field;
		}

		public String getColumnName() {
			return columnName;
		}

		public int getLength() {
			return length;
		}

		public int getType() {
			return type;
		}
		
		public PropertyDescriptor getPd() {
			return pd;
		}

		public Method getWriteMethod() {
			return writeMethod;
		}

		public Method getReadMethod() {
			return readMethod;
		}

		public ColumnDescriptor(Field field, PropertyDescriptor pd, String columnName, int length,
				int type, boolean pk) {
			super();
			this.field = field;
			this.pd = pd;
			this.readMethod = pd.getReadMethod();
			this.writeMethod = pd.getWriteMethod();
			this.columnName = columnName;
			this.length = length;
			this.type = type;
			this.pk = pk;
		}

	}

	private Class clazz;

	private String tableName;

	private List<ColumnDescriptor> columns;

	/**
	 * 主键
	 */
	private List<ColumnDescriptor> pkColumns;

	public List<ColumnDescriptor> getPkColumns() {
		return pkColumns;
	}
	
	public List<ColumnDescriptor> getNotPkColumns() {
		return columns.subList(0, columns.size() - pkColumns.size());
	}

	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * 返回所有列，而且PK Columns永远是最后几列。
	 * @return
	 */
	public List<ColumnDescriptor> getColumns() {
		return columns;
	}

	public TableDescriptor(Class clazz) {
		this.clazz = clazz;
		if (clazz.isAnnotationPresent(Table.class)) {
			// 该class存在Table类型的注解，获取指定的表名
			Table table = (Table) clazz.getAnnotation(Table.class);
			tableName = table.name();
		} else {
			tableName = clazz.getName();
		}
		// 获取bean所声明的成员变量(include private)
		Field[] fields = clazz.getDeclaredFields();
		columns = new ArrayList<ColumnDescriptor>();

		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			// 判断该成员变量上是不是存在Column类型的注解
			if (!f.isAnnotationPresent(Column.class)) {
				continue;
			}

			Column c = f.getAnnotation(Column.class);// 获取实例
			// 获取元素值
			String columnName = c.name();
			// 如果未指定列名，默认列名使用成员变量名
			if ("".equals(BaseSpringDaoUtils.killNull(columnName))) {
				columnName = f.getName();
			}

			int columnLength = c.length();

			int type = c.type();
			if (type == Types.OTHER) {
				type = detectType(f);
			}
			
			PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(clazz, f.getName());

			columns.add(new ColumnDescriptor(f, pd, columnName, columnLength, type,
					c.pk()));
		}

		pkColumns = new ArrayList<TableDescriptor.ColumnDescriptor>();
		for (ColumnDescriptor c : columns) {
			if (c.isPk()) {
				pkColumns.add(c);
			}
		}
		
		columns.removeAll(pkColumns);
		
		columns.addAll(pkColumns);
	}

	/**
	 * 根据Oracle网站提供的Data Mapping，探测属性的类型。
	 * 
	 * @param f
	 * @return
	 */
	private int detectType(Field f) {
		Class fieldType = f.getType();

		if (String.class == fieldType) {
			return Types.VARCHAR; // 也有可能是CHAR或者LONGVARCHAR，但是默认还是VARCHAR
		} else if (int.class == fieldType || Integer.class == fieldType) {
			return Types.INTEGER;
		} else if (long.class == fieldType || Long.class == fieldType) {
			return Types.BIGINT;
		} else if (BigDecimal.class == fieldType) {
			return Types.DECIMAL; // 也有可能是NUMERIC，但默认还是DECIMAL
		} else if (boolean.class == fieldType || Boolean.class == fieldType) {
			return Types.BIT;
		} else if (byte.class == fieldType || Byte.class == fieldType) {
			return Types.TINYINT;
		} else if (float.class == fieldType || Float.class == fieldType) {
			return Types.REAL;
		} else if (double.class == fieldType || Double.class == fieldType) {
			return Types.DOUBLE;
		} else if (java.sql.Date.class == fieldType) {
			return Types.DATE;
		} else if (java.sql.Time.class == fieldType) {
			return Types.TIME;
		} else if (java.sql.Timestamp.class == fieldType) {
			return Types.TIMESTAMP;
		}

		throw new IllegalStateException(
				"Failed to detect field db type. Field: " + f.toGenericString());
	}
	
}
