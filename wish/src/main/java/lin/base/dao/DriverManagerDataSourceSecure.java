package lin.base.dao;

import java.sql.SQLException;

import lin.wish.security.DBPasswordEncoder;
import oracle.jdbc.pool.OracleDataSource;


public class DriverManagerDataSourceSecure extends OracleDataSource {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DriverManagerDataSourceSecure() throws SQLException {
		super();
	}

	private boolean secure;

	public boolean isSecure() {
		return secure;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	@Override
	public void setPassword(String password) {
		try {
			password = (new DBPasswordEncoder()).decrypt(password);
		} catch (Throwable e) {
			throw new IllegalStateException("数据库密码解密错误", e);
		}
		super.setPassword(password);
	}

}
