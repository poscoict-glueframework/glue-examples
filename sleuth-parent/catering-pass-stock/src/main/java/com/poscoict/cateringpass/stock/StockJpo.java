package com.poscoict.cateringpass.stock;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.uuid.Generators;
import com.poscoict.glueframework.util.JsonSerializable;

@Entity(name = "StockJpo")
public class StockJpo implements JsonSerializable {
	@Id
	private String uuid;
	private String orderId; // OrderID
	private String status;

	@SuppressWarnings("unused")
	private StockJpo() {
	}

	public StockJpo(String orderId) {
		this.uuid = Generators.timeBasedGenerator().generate().toString();
		this.orderId = orderId;
		this.status = "init";
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
