package lin.wish.security;

import java.nio.charset.Charset;

public class DBPasswordEncoder {

	private static final String key = "w5.fjzDfK/gAMesD";

	AES aes;

	public void setKey(String key) {
		aes.setKey(key);
	}

	public DBPasswordEncoder() {
		aes = new AES();
		aes.setKey(key);
	}

	public String encrypt(String src) {
		return Base64Util.encode(aes.Encrypt(src).getBytes(Charset.forName("utf-8")));
	}

	public String decrypt(String cpt) {
		return aes.Decrypt(new String(Base64Util.decode(cpt), Charset.forName("utf-8")));
	}

}
