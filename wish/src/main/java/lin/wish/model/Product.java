package lin.wish.model;

import lin.base.dao.Column;
import lin.base.dao.Table;

@Table(name="product")
public class Product {
	@Column(pk=true)
	long id;   //一条记录的编号。
	
	@Column
	String name;//名称
	
	@Column
	String code;//编号
	
	@Column
	String company;
	
	@Column
	String url;
	
	@Column
	double price;
	
	@Column
	String description;
	
	@Column
	String img_url;//图像地址
	
	@Column
	String offers;//供货商
	
	@Column
	String delivery;//快递
	
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public String getOffers() {
		return offers;
	}

	public void setOffers(String offers) {
		this.offers = offers;
	}

	public String getDelivery() {
		return delivery;
	}

	public void setDelivery(String delivery) {
		this.delivery = delivery;
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

	public Product(long id, String name, String code, String company,
			String url, double price, String description, String img_url,
			String offers, String delivery, String created_at, String updated_at) {
		super();
		this.id = id;
		this.name = name;
		this.code = code;
		this.company = company;
		this.url = url;
		this.price = price;
		this.description = description;
		this.img_url = img_url;
		this.offers = offers;
		this.delivery = delivery;
		this.created_at = created_at;
		this.updated_at = updated_at;
	}
	
	public Product(){}
}
