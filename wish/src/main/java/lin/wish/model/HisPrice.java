package lin.wish.model;

import lin.base.dao.Column;
import lin.base.dao.Table;


//CREATE TABLE his_price(
//	    ID			NUMBER(32) NOT NULL PRIMARY KEY,
//	    product_id  NUMBER(32),
//      name		varchar2(500),
//		CODE    	VARCHAR2(100),
//		COMPANY    	VARCHAR2(100),
//		PRICE   	NUMBER(32),
//		DESCRIPTION 	VARCHAR2(500),  --增加对价格的描述，必然促销价等。 
//	    created_at  VARCHAR2(50),
//	    updated_at  VARCHAR2(50)
//	);
//
//	CREATE SEQUENCE SEQ_PRICE_ID 
//	INCREMENT BY 1  
//	START WITH 1 
//	NOMAXvalue 
//	NOCYCLE
//	NOCACHE;

@Table(name="his_price")
public class HisPrice {
	
	@Column(pk=true)
	long id;
	
	@Column
	long product_id;
	
	@Column
	String name;
	
	@Column
	String code;
	
	@Column
	String company;
	
	@Column
	double price;
	
	@Column
	String description;
	
	@Column
	String created_at;
	
	@Column
	String updated_at;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getProduct_id() {
		return product_id;
	}

	public void setProduct_id(long product_id) {
		this.product_id = product_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public HisPrice(long id, long product_id, String name, String code,
			String company, double price, String description,
			String created_at, String updated_at) {
		super();
		this.id = id;
		this.product_id = product_id;
		this.name = name;
		this.code = code;
		this.company = company;
		this.price = price;
		this.description = description;
		this.created_at = created_at;
		this.updated_at = updated_at;
	}

	/**
	 * defualt constructor
	 * */
	public HisPrice() {
	}
}
