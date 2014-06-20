package it.genpwd;

import sge.security.DBPasswordEncoder;

/**
 * Hello world!
 * 
 */
public class Genpwd {
	public static void main(String[] args) {
		if (args.length == 1) {
			String cryptString = doGenpwd(args[0].trim());
			System.out.println("加密结果：" + cryptString);
		}
	}

	private static String doGenpwd(String password) {
		
		DBPasswordEncoder encoder = new DBPasswordEncoder();

		String cryptString = encoder.encrypt(password);

		String restored = encoder.decrypt(cryptString);

		if (!password.equals(restored)) {
			// System.out.println("加密后解密的内容为：" + restored);
			throw new IllegalStateException("加密错误，加密结果无法解密");
		}
		return cryptString;
	}
}
