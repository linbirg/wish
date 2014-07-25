package lin.wish.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import lin.base.dao.BaseSpringDao;
import lin.wish.model.Product;

public class ProductDao extends BaseSpringDao<Product> {

	public ProductDao() {
		super(new RowMapper<Product>() {

			@Override
			public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Product(rs.getLong("id"), rs.getString("name"),
						rs.getString("code"), rs.getString("company"),
						rs.getString("url"), rs.getDouble("price"),
						rs.getString("description"), rs.getString("img_url"),
						rs.getString("offers"), rs.getString("delivery"),
						rs.getString("created_at"), rs.getString("updated_at"));
			}
		});
	}
	
	@Override
	public int insert(Product e) {
		long id = getNextID();
		e.setId(id);
		return super.insert(e);
	}

	private long getNextID() {
		return jdbcTemplate.queryForObject("select SQ_PRODUCT_ID.nextval from dual", Long.class);
	}
}
