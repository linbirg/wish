package lin.wish.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import lin.base.dao.BaseSpringDao;
import lin.wish.model.HisPrice;

import org.springframework.jdbc.core.RowMapper;

public class HisPriceDao extends BaseSpringDao<HisPrice> {

	public HisPriceDao() {
		super(new RowMapper<HisPrice>() {

			@Override
			public HisPrice mapRow(ResultSet rs, int rowNum)
					throws SQLException {

				return new HisPrice(rs.getLong("id"), rs.getLong("product_id"),
						rs.getString("name"), rs.getString("code"),
						rs.getString("company"), rs.getDouble("price"),
						rs.getString("description"),
						rs.getString("created_at"), rs.getString("updated_at"));
			}
		});
	}

	@Override
	public int insert(HisPrice e) {
		long id = getNextID();
		e.setId(id);
		return super.insert(e);
	}

	// {{private mothed
	private long getNextID() {
		return jdbcTemplate.queryForObject(
				"select SEQ_PRICE_ID.nextval from dual", Long.class);
	}

	// }}
}
