package lin.wish.BerkeleyDB;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.Iterator;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;

/**
 * Fast queue implementation on top of Berkley DB Java Edition.
 * 
 * 
 * This class is thread-safe.
 */
public class BDBPersistentQueue<T> implements AutoCloseable {

	/**
	 * Berkley DB environment
	 */
	private final Environment dbEnv;

	/**
	 * Berkley DB instance for the queue
	 */
	private Database queueDatabase;

	/**
	 * Queue cache size - number of element operations it is allowed to loose in
	 * case of system crash.
	 */
	private final int cacheSize;

	/**
	 * This queue name.
	 */
	private final String queueName;

	/**
	 * Using deferred writing
	 */
	private final boolean deferred;

	/**
	 * Database config
	 */
	private final DatabaseConfig dbConfig;

	/**
	 * Queue operation counter, which is used to sync the queue database to disk
	 * periodically.
	 */
	private int opsCounter;

	private Cursor cursor = null;

	/**
	 * Creates instance of persistent queue.
	 * 
	 * @param queueEnvPath
	 *            queue database environment directory path
	 * @param queueName
	 *            descriptive queue name
	 * @param cacheSize
	 *            how often to sync the queue to disk
	 */
	public BDBPersistentQueue(final String queueName,
			final String queueEnvPath, final int cacheSize) {
		// Create parent dirs for queue environment directory

		File file = new File(queueEnvPath);
		if (!file.exists())
			file.mkdirs();

		// Setup database environment
		final EnvironmentConfig dbEnvConfig = new EnvironmentConfig();
		dbEnvConfig.setTransactional(false);
		dbEnvConfig.setAllowCreate(true);
		try {
			this.dbEnv = new Environment(file, dbEnvConfig);

			// Setup non-transactional deferred-write queue database
			dbConfig = new DatabaseConfig();
			dbConfig.setTransactional(false);
			dbConfig.setAllowCreate(true);
			if (cacheSize > 0) {
				deferred = true;
			} else {
				deferred = false;
			}
			dbConfig.setDeferredWrite(deferred);

			dbConfig.setBtreeComparator(new KeyComparator());
			this.queueDatabase = dbEnv.openDatabase(null, queueName, dbConfig);
			this.queueName = queueName;
			this.cacheSize = cacheSize;
			this.opsCounter = 0;
		} catch (DatabaseException e) {
			throw new IllegalStateException(e);
		}
	}

	public BDBPersistentQueue(final String queueName, final String queueEnvPath) {
		this(queueName, queueEnvPath, 0);
	}

	/**
	 * Retrieves and returns element from the head of this queue.
	 * 
	 * @return element from the head of the queue or null if queue is empty
	 * 
	 * @throws IllegalStateException
	 *             :ClassNotFound
	 */
	@SuppressWarnings("unchecked")
	public T poll() {
		final DatabaseEntry key = new DatabaseEntry();
		final DatabaseEntry data = new DatabaseEntry();
		try (Cursor cursor = queueDatabase.openCursor(null, null)) {
			cursor.getFirst(key, data, LockMode.RMW);
			if (data.getData() == null)
				return null;
			
			T result;
			try (ByteArrayInputStream bis = new ByteArrayInputStream(
					data.getData());
					ObjectInput in = new ObjectInputStream(bis)) {
				result = (T) in.readObject();
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(e);
			} catch (IOException ioe) {
				throw new IllegalStateException(ioe);
			}

			cursor.delete();
			if (deferred) {
				opsCounter++;
				if (opsCounter >= cacheSize) {
					queueDatabase.sync();
					opsCounter = 0;
				}
			}
			return result;
		}
	}

	/**
	 * Pushes element to the tail of this queue.
	 * 
	 * @param element
	 *            element
	 * 
	 * @throws IllegalStateException
	 *             in case of disk IO failure
	 */
	public synchronized void push(Serializable element) {
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		try (Cursor cursor = queueDatabase.openCursor(null, null)) {
			cursor.getLast(key, data, LockMode.RMW);

			BigInteger prevKeyValue;
			if (key.getData() == null) {
				prevKeyValue = BigInteger.valueOf(-1);// 从0开始计数key值。
			} else {
				prevKeyValue = new BigInteger(key.getData());
			}
			BigInteger newKeyValue = prevKeyValue.add(BigInteger.ONE);

			final DatabaseEntry newKey = new DatabaseEntry(
					newKeyValue.toByteArray());

			try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutput out = new ObjectOutputStream(bos)) {
				out.writeObject(element);
				byte[] bytes = bos.toByteArray();
				final DatabaseEntry newData = new DatabaseEntry(bytes);
				queueDatabase.put(null, newKey, newData);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}

			if (deferred) {
				opsCounter++;
				if (opsCounter >= cacheSize) {
					queueDatabase.sync();
					opsCounter = 0;
				}
			}
		}
	}

//	/**
//	 * Retrieves and returns element from the head of this queue.
//	 * 
//	 * @return element from the head of the queue or null if queue is empty
//	 * 
//	 * @throws IllegalStateException
//	 *             :ClassNotFound
//	 */
//	@SuppressWarnings("unchecked")
//	public T getNext() {
//		final DatabaseEntry key = new DatabaseEntry();
//		final DatabaseEntry data = new DatabaseEntry();
//		if (cursor == null) {
//			cursor = queueDatabase.openCursor(null, null);
//		}
//		
//		OperationStatus status= cursor.getNext(key, data, LockMode.RMW);
//		if (status != OperationStatus.SUCCESS) {
//			cursor.close();
//			cursor = null;
//			return null;
//		}
//		
//		if (data.getData() == null)
//			return null;
//
//		T result;
//		try (ByteArrayInputStream bis = new ByteArrayInputStream(data.getData());
//				ObjectInput in = new ObjectInputStream(bis)) {
//			result = (T) in.readObject();
//		} catch (ClassNotFoundException e) {
//			throw new IllegalStateException(e);
//		} catch (IOException ioe) {
//			throw new IllegalStateException(ioe);
//		}
//		return result;
//	}
	
	/**
	 * 提供iterator，以便于遍历。
	 * */
	public Iterator<T> getIterator(){
		BDBPQueneIterator iterator = new BDBPQueneIterator();
		iterator.setQueueDatabase(queueDatabase);
		
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		try(Cursor cursor = queueDatabase.openCursor(null, null)){
			cursor.getLast(key, data, LockMode.RMW);

			BigInteger lastKeyValue;
			if (key.getData() == null) {
				lastKeyValue = BigInteger.valueOf(-1);// 从0开始计数key值。
			} else {
				lastKeyValue = new BigInteger(key.getData());
			}
			
			iterator.setLastKey(lastKeyValue);
		}
		return iterator;
	}

	/**
	 * Returns the size of this queue.
	 * 
	 * @return the size of the queue
	 */
	public long size() {
		return queueDatabase.count();
	}

	/**
	 * Returns this queue name.
	 * 
	 * @return this queue name
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * Closes this queue and frees up all resources associated to it.
	 */
	@Override
	public void close() {
		if (cursor != null) {
			cursor.close();
		}
		queueDatabase.close();
		dbEnv.close();
	}

	/**
	 * Clear database
	 */
	public void clear() {
		queueDatabase.close();
		dbEnv.truncateDatabase(null, getQueueName(), false);
		this.queueDatabase = dbEnv.openDatabase(null, queueName, dbConfig);
	}

	/**
	 * Key comparator for DB keys
	 */
	static class KeyComparator implements Comparator<byte[]>, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -6511810441909883995L;

		/**
		 * Compares two DB keys.
		 * 
		 * @param key1
		 *            first key
		 * @param key2
		 *            second key
		 * 
		 * @return comparison result
		 */
		@Override
		public int compare(byte[] o1, byte[] o2) {
			return new BigInteger((byte[]) o1).compareTo(new BigInteger(
					(byte[]) o2));
		}

	}
	
	//{{iterator
	public class BDBPQueneIterator implements Iterator<T>{
		private BigInteger currentKey;
		private BigInteger lastKey;
		private Database queueDatabase;
		
		
		public BDBPQueneIterator() {
			super();
			currentKey = BigInteger.valueOf(-1);
		}

		@Override
		public boolean hasNext() {
			return (currentKey.compareTo(getLastKey()) < 0);
		}

		
		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			if (!hasNext()) {
				return null;
			}
			
			currentKey = currentKey.add(BigInteger.ONE);
			final DatabaseEntry curKey = new DatabaseEntry(
					currentKey.toByteArray());
			final DatabaseEntry data = new DatabaseEntry();
			getQueueDatabase().get(null, curKey, data, LockMode.DEFAULT);
			
			if (data.getData() == null) {
				return null;
			}
			
			T result;
			try (ByteArrayInputStream bis = new ByteArrayInputStream(
					data.getData());
					ObjectInput in = new ObjectInputStream(bis)) {
				result = (T) in.readObject();
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(e);
			} catch (IOException ioe) {
				throw new IllegalStateException(ioe);
			}
			return result;
		}

		public Database getQueueDatabase() {
			return queueDatabase;
		}

		public void setQueueDatabase(Database queueDatabase) {
			this.queueDatabase = queueDatabase;
		}

		public BigInteger getLastKey() {
			return lastKey;
		}

		public void setLastKey(BigInteger lastKey) {
			this.lastKey = lastKey;
		}
	}
	
	//}}

}
