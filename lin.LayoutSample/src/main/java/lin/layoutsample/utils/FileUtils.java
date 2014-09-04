package lin.layoutsample.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

	private static Logger logger = LoggerFactory.getLogger(FileUtils.class);
	
	public static String DEFAULT_FILE_ENCODING = "utf-8";

	// {{file create
	public static boolean createFile(String destFileName) {
		File file = new File(destFileName);
		if (file.exists()) {
			logger.info("创建单个文件" + destFileName + "失败，目标文件已存在");
			return false;
		}
		if (destFileName.endsWith(File.separator)) {
			logger.info("创建单个文件" + destFileName + "失败，目标文件不能为目录");
			return false;
		}
		// 判断目标文件�?在的目录是否存在
		if (!file.getParentFile().exists()) {
			// 如果目标文件�?在的目录不存在，则创建父目录
			logger.info("目标文件所在目录不存在，准备创建它。");
			if (!file.getParentFile().mkdirs()) {
				logger.info("创建目标文件所在目录失败！");
				return false;
			}
		}
		// 创建目标文件
		try {
			if (file.createNewFile()) {
				logger.info("创建单个文件" + destFileName + "成功。");
				return true;
			} else {
				logger.info("创建单个文件" + destFileName + "失败。");
				return false;
			}
		} catch (IOException e) {
			logger.info("创建单个文件" + destFileName + "失败。", e);
			return false;
		}
	}

	public static boolean createDir(String destDirName) {
		File dir = new File(destDirName);
		if (dir.exists()) {
			logger.info("创建目录" + destDirName + "失败，目标目录已经存。");
			return false;
		}
		if (!destDirName.endsWith(File.separator)) {
			destDirName = destDirName + File.separator;
		}
		// 创建目录
		if (dir.mkdirs()) {
			logger.info("创建目录" + destDirName + "成功。");
			return true;
		} else {
			logger.info("创建目录" + destDirName + "失败。");
			return false;
		}
	}

	public static String createTempFile(String prefix, String suffix,
			String dirName) {
		File tempFile = null;
		if (dirName == null) {
			try {
				// 在默认文件夹下创建临时文�?
				tempFile = File.createTempFile(prefix, suffix);
				// 返回临时文件的路�?
				return tempFile.getCanonicalPath();
			} catch (IOException e) {

				logger.info("创建临时文件失败。" + e.getMessage());
				return null;
			}
		} else {
			File dir = new File(dirName);
			// 如果临时文件�?在目录不存在，首先创�?
			if (!dir.exists()) {
				if (!FileUtils.createDir(dirName)) {
					logger.info("创建临时文件失败，不能创建临时文件所在的目录。");
					return null;
				}
			}
			try {
				// 在指定目录下创建临时文件
				tempFile = File.createTempFile(prefix, suffix, dir);
				return tempFile.getCanonicalPath();
			} catch (IOException e) {
				logger.info("创建临时文件失败。" + e.getMessage());
				return null;
			}
		}
	}

	// }}

	// {{file copy
	/**
	 * 
	 * 使用文件通道的方式复制文件
	 * 
	 * @param s
	 *            源文件
	 * @param t
	 *            复制到的新文件
	 * 
	 */
	public static void fileChannelCopy(String s_name, String t_name) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		try {

			File s = new File(s_name);
			File t = new File(t_name);
			if (!s.exists()) {
				logger.error("源文件不存在：" + s_name);
				throw new IllegalStateException("源文件不存在：" + s_name);
			}

			fi = new FileInputStream(s);
			fo = new FileOutputStream(t);
			in = fi.getChannel();// 得到对应的文件通道
			out = fo.getChannel();// 得到对应的文件通道
			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				fi.close();
				in.close();
				fo.close();
				out.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}

	// }}

	// {{file r/w

	public static String read(String file_name) {
		return read(file_name, DEFAULT_FILE_ENCODING);
	}
	
	public static String read(String file_name,String encoding) {
		InputStreamReader inputReader = null;
		BufferedReader bufferReader = null;
		InputStream inputStream = null;
		StringBuffer strBuffer = null;

		try {
			inputStream = new FileInputStream(file_name);
			inputReader = new InputStreamReader(inputStream,encoding);
			bufferReader = new BufferedReader(inputReader);

			// 读取�?�?
			String line = null;
			strBuffer = new StringBuffer();

			while ((line = bufferReader.readLine()) != null) {
				strBuffer.append(line);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				bufferReader.close();
				inputReader.close();
				inputStream.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}

		return strBuffer.toString();
	}

	// 写文件
	public static void write(String name,String content) {
		write(name, content,DEFAULT_FILE_ENCODING);
	}
	
	public static void write(String name,String content,String encoding) {
		BufferedWriter fw = null;
		try {
			File file = new File(name);
			fw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), encoding)); // 指定编码格式，以免读取时中文字符异常
			fw.write(content);
			fw.flush(); // 全部写入缓存中的内容
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
			}
		}
	}
	
	

	// }}
}
