package BerkeleyDB;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;

public class BerkeleyDBHelper {
	private Logger logger = LoggerFactory.getLogger(BerkeleyDBHelper.class);

	// 数据库环境
	private Environment myDbEnvironment = null;
	// 数据库配置
	private DatabaseConfig dbConfig = null;
	// //数据库游标
	// private Cursor myCursor = null;
	// 数据库对象
	private Database database = null;
	// 数据库文件名
	private String fileName = "";
	// 数据库名称
	private String dbName = "";

	/**
	 * 打开当前数据库
	 */
	public void open(String dbName, String fileName) {
		try {
			this.setDbName(dbName);
			this.setFileName(fileName);

			logger.info("打开数据库: " + this.getDbName());
			EnvironmentConfig envConfig = new EnvironmentConfig();
			envConfig.setAllowCreate(true);
			envConfig.setTransactional(true);
			envConfig.setReadOnly(false);
			envConfig.setTxnTimeoutVoid(10000);
			envConfig.setLockTimeoutVoid(10000);
			/*
			 * 其他配置 可以进行更改 EnvironmentMutableConfig envMutableConfig = new
			 * EnvironmentMutableConfig();
			 * envMutableConfig.setCachePercent(50);//设置je的cache占用jvm 内存的百分比。
			 * envMutableConfig.setCacheSize(123456);//设定缓存的大小为123456Bytes
			 * envMutableConfig
			 * .setTxnNoSync(true);//设定事务提交时是否写更改的数据到磁盘，true不写磁盘。
			 * //envMutableConfig
			 * .setTxnWriteNoSync(false);//设定事务在提交时，是否写缓冲的log到磁盘
			 * 。如果写磁盘会影响性能，不写会影响事务的安全。随机应变。
			 */
			File file = new File(this.getFileName());
			if (!file.exists())
				file.mkdirs();
			myDbEnvironment = new Environment(file, envConfig);

			dbConfig = new DatabaseConfig();
			dbConfig.setAllowCreate(true);
			dbConfig.setTransactional(true);
			dbConfig.setReadOnly(false);
			// dbConfig.setSortedDuplicates(false);
			/*
			 * setBtreeComparator 设置用于B tree比较的比较器，通常是用来排序
			 * setDuplicateComparator 设置用来比较一个key有两个不同值的时候的大小比较器。
			 * setSortedDuplicates 设置一个key是否允许存储多个值，true代表允许，默认false.
			 * setExclusiveCreate 以独占的方式打开，也就是说同一个时间只能有一实例打开这个database。
			 * setReadOnly 以只读方式打开database,默认是false. setTransactional
			 * 如果设置为true,则支持事务处理，默认是false，不支持事务。
			 */
			if (getDatabase() == null)
				setDatabase(myDbEnvironment
						.openDatabase(null, dbName, dbConfig));

			logger.info(dbName + "数据库中的数据个数: " + getDatabase().count());
			/*
			 * Database.getDatabaseName() 取得数据库的名称 如：String dbName =
			 * myDatabase.getDatabaseName();
			 * 
			 * Database.getEnvironment() 取得包含这个database的环境信息 如：Environment
			 * theEnv = myDatabase.getEnvironment();
			 * 
			 * Database.preload() 预先加载指定bytes的数据到RAM中。
			 * 如：myDatabase.preload(1048576l); // 1024*1024
			 * 
			 * Environment.getDatabaseNames() 返回当前环境下的数据库列表
			 * Environment.removeDatabase() 删除当前环境中指定的数据库。 如： String dbName =
			 * myDatabase.getDatabaseName(); myDatabase.close();
			 * myDbEnv.removeDatabase(null, dbName);
			 * 
			 * Environment.renameDatabase() 给当前环境下的数据库改名 如： String oldName =
			 * myDatabase.getDatabaseName(); String newName = new String(oldName
			 * + ".new", "UTF-8"); myDatabase.close();
			 * myDbEnv.renameDatabase(null, oldName, newName);
			 * 
			 * Environment.truncateDatabase() 清空database内的所有数据，返回清空了多少条记录。 如：
			 * Int numDiscarded= myEnv.truncate(null,
			 * myDatabase.getDatabaseName(),true);
			 * CheckMethods.PrintDebugMessage("一共删除了 " + numDiscarded
			 * +" 条记录 从数据库 " + myDatabase.getDatabaseName());
			 */
		} catch (DatabaseException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 使用属性值打开数据库。使用之前请确保dbname和filename属性已经设置。
	 * */
	public void open() {
		open(this.getDbName(), this.getFileName());
	}

	/**
	 * 向数据库中写入记录 传入key和value
	 */
	public boolean insert(String key, String value, boolean isOverwrite) {
		try {
			// 设置key/value,注意DatabaseEntry内使用的是bytes数组
			DatabaseEntry theKey = new DatabaseEntry(key.trim().getBytes(
					"UTF-8"));
			DatabaseEntry theData = new DatabaseEntry(value.getBytes("UTF-8"));
			OperationStatus res = null;
			Transaction txn = null;
			try {
				TransactionConfig txConfig = new TransactionConfig();
				txConfig.setSerializableIsolation(true);
				txn = myDbEnvironment.beginTransaction(null, txConfig);
				if (isOverwrite) {
					res = getDatabase().put(txn, theKey, theData);
				} else {
					res = getDatabase().putNoOverwrite(txn, theKey, theData);
				}
				txn.commit();
				if (res == OperationStatus.SUCCESS) {
					logger.info("向数据库" + getDbName() + "中写入:" + key + ","
							+ value);
					return true;
				} else if (res == OperationStatus.KEYEXIST) {
					logger.error("向数据库" + getDbName() + "中写入:" + key + ","
							+ value + "失败,该值已经存在");
					return false;
				} else {
					logger.error("向数据库" + getDbName() + "中写入:" + key + ","
							+ value + "失败");
					return false;
				}
			} catch (DatabaseException databaseException) {
				txn.abort();
				logger.error("向数据库" + getDbName() + "中写入:" + key + "," + value
						+ "出现异常");
				logger.error(databaseException.getMessage());
				logger.error(databaseException.getCause().toString());
				return false;
			}
		} catch (Throwable e) {
			// 错误处理
			logger.error("向数据库" + getDbName() + "中写入:" + key + "," + value
					+ "出现错误");
			return false;
		}
	}

	/**
	 * 默认采用覆盖的方式插入。
	 * */
	public boolean insert(String key, String value) {
		return insert(key, value, true);
	}

	/*
	 * 关闭当前数据库
	 */
	public void close() {
		try {
			if (getDatabase() != null) {
				getDatabase().close();
			}
			if (myDbEnvironment != null) {
				logger.info("关闭数据库: " + getDbName());
				myDbEnvironment.cleanLog();
				myDbEnvironment.close();
			}
		} catch (DatabaseException e) {
			String errMsg = String.format("关闭数据库%s时出现异常[%s]", getDbName(),
					e.getMessage());
			throw new IllegalStateException(errMsg);
		}
	}

	/**
	 * 删除数据库中的一条记录
	 */
	public boolean delete(String key) {
		boolean success = false;
		long sleepMillis = 0;
		for (int i = 0; i < 3; i++) {
			if (sleepMillis != 0) {
				try {
					Thread.sleep(sleepMillis);
				} catch (InterruptedException e) {
					logger.info(e.getMessage());
				}
				sleepMillis = 0;
			}
			Transaction txn = null;
			try {
				TransactionConfig txConfig = new TransactionConfig();
				txConfig.setSerializableIsolation(true);
				txn = myDbEnvironment.beginTransaction(null, txConfig);
				DatabaseEntry theKey;
				theKey = new DatabaseEntry(key.trim().getBytes("UTF-8"));
				OperationStatus res = getDatabase().delete(txn, theKey);
				txn.commit();
				if (res == OperationStatus.SUCCESS) {
					logger.info("从数据库" + getDbName() + "中删除:" + key);
					success = true;
					return success;
				} else if (res == OperationStatus.KEYEMPTY) {
					logger.warn("没有从数据库" + getDbName() + "中找到:" + key + "。无法删除");
				} else {
					logger.warn("删除操作失败，由于" + res.toString());
				}
				return false;
			} catch (UnsupportedEncodingException e) {
				throw new IllegalStateException(e);
			} catch (DatabaseException databaseException) {
				logger.warn("删除操作失败，出现lockConflict异常");
				logger.warn(databaseException.getMessage());
				logger.warn(databaseException.getCause().toString());
				sleepMillis = 1000;

				continue;
			} finally {
				if (!success) {
					if (txn != null) {
						try {
							txn.abort();
						} catch (DatabaseException e) {
							logger.warn("回滚操作失败" + e.getMessage());
						}
					}
				}
			}
		}
		return false;
	}

	/*
	 * 从数据库中读出数据 传入key 返回value
	 */
	public String read(String key) {
		try {
			DatabaseEntry theKey = new DatabaseEntry(key.trim().getBytes(
					"UTF-8"));
			DatabaseEntry theData = new DatabaseEntry();
			Transaction txn = null;
			try {
				TransactionConfig txConfig = new TransactionConfig();
				txConfig.setSerializableIsolation(true);
				txn = myDbEnvironment.beginTransaction(null, txConfig);
				OperationStatus res = getDatabase().get(txn, theKey, theData,
						LockMode.DEFAULT);
				txn.commit();
				if (res == OperationStatus.SUCCESS) {
					byte[] retData = theData.getData();
					String foundData = new String(retData, "UTF-8");
					logger.debug("从数据库" + getDbName() + "中读取:" + key + ","
							+ foundData);
					return foundData;
				} else {
					logger.warn("No record found for key '" + key + "'.");
					return "";
				}
			} catch (DatabaseException databaseException) {
				txn.abort();
				logger.warn("从数据库" + getDbName() + "中读取:" + key + "出现lock异常");
				logger.warn(databaseException.getMessage());
				logger.warn(databaseException.getCause().toString());

				return "";
			}

		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * 测试是否含有指定键值。函数采用一次读取操作，不使用事务。
	 * */
	public boolean contains(String key) {
		try {
			DatabaseEntry theKey = new DatabaseEntry(key.trim().getBytes(
					"UTF-8"));
			DatabaseEntry theData = new DatabaseEntry();
			try {
				OperationStatus res = getDatabase().get(null, theKey, theData,
						LockMode.DEFAULT);
				if (res == OperationStatus.SUCCESS) {
					return true;
				} else {
					return false;
				}
			} catch (DatabaseException databaseException) {
				return false;
			}
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}

	/*
	 * 遍历数据库中的所有记录，返回list
	 */
	public ArrayList<String> getAll() {
		logger.debug("===========遍历数据库" + getDbName() + "中的所有数据==========");
		Cursor myCursor = null;
		ArrayList<String> resultList = new ArrayList<String>();
		Transaction txn = null;
		try {
			txn = this.myDbEnvironment.beginTransaction(null, null);
			CursorConfig cc = new CursorConfig();
			cc.setReadCommitted(true);
			if (myCursor == null)
				myCursor = getDatabase().openCursor(txn, cc);
			DatabaseEntry foundKey = new DatabaseEntry();
			DatabaseEntry foundData = new DatabaseEntry();
			// 使用cursor.getPrev方法来遍历游标获取数据
			if (myCursor.getFirst(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
				String theKey = new String(foundKey.getData(), "UTF-8");
				String theData = new String(foundData.getData(), "UTF-8");
				resultList.add(theKey);
				logger.debug("Key | Data : " + theKey + " | " + theData + "");
				while (myCursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
					theKey = new String(foundKey.getData(), "UTF-8");
					theData = new String(foundData.getData(), "UTF-8");
					resultList.add(theKey);
					logger.debug("Key | Data : " + theKey + " | " + theData + "");
				}
			}
			myCursor.close();
			txn.commit();
			return resultList;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		} catch (Throwable e) {
			logger.warn("getEveryItem处理出现异常");
			logger.warn(e.getMessage().toString());
			logger.warn(e.getCause().toString());

			try {
				txn.abort();
				if (myCursor != null) {
					myCursor.close();
				}

			} catch (DatabaseException e1) {
				throw new IllegalStateException(e1);
			}
			return null;
		}
	}
	
	public long size() {
		try {
			return getDatabase().count();
		} catch (DatabaseException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public boolean isEmpty(){
		return (size() == 0);
	}

	/**
	 * 获取数据库的第一个数据，返回第一个数据的key。
	 */
	public String getFirst() {
		return getFirstOrLast("first");
	}

	/**
	 * 获取数据库的第一个数据，返回最后一个数据的key。
	 */
	public String getLast() {
		return getFirstOrLast("last");
	}

	public Database getDatabase() {
		return database;
	}

	public void setDatabase(Database database) {
		this.database = database;
	}

	private String getFirstOrLast(String firstOrLast) {
		Cursor myCursor = null;
		String resultKey = "";
		Transaction txn = null;
		try {
			txn = this.myDbEnvironment.beginTransaction(null, null);
			CursorConfig cc = new CursorConfig();
			cc.setReadCommitted(true);
			if (myCursor == null)
				myCursor = getDatabase().openCursor(txn, cc);
			DatabaseEntry foundKey = new DatabaseEntry();
			DatabaseEntry foundData = new DatabaseEntry();
			
			OperationStatus status = null;
			if (firstOrLast.equals("first")) {
				status = myCursor.getFirst(foundKey, foundData,
						LockMode.DEFAULT);
			} else {
				status = myCursor
						.getLast(foundKey, foundData, LockMode.DEFAULT);
			}

			if (status == OperationStatus.SUCCESS) {
				String theKey = new String(foundKey.getData(), "UTF-8");
				String theData = new String(foundData.getData(), "UTF-8");
				resultKey = theKey;
				logger.debug("Key | Data : " + theKey + " | " + theData + "");
			}else {
				logger.error("getFirstOrLast:读取操作不成功。");
			}
			myCursor.close();
			txn.commit();
			return resultKey;
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		} catch (Throwable e) {
			logger.warn("getEveryItem处理出现异常");
			logger.warn(e.getMessage().toString());
			logger.warn(e.getCause().toString());

			try {
				txn.abort();
				if (myCursor != null) {
					myCursor.close();
				}

			} catch (DatabaseException e1) {
				throw new IllegalStateException(e1);
			}
			return null;
		}
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
}
