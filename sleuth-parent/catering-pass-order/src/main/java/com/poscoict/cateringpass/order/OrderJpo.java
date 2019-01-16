package com.poscoict.cateringpass.order;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.uuid.Generators;
import com.poscoict.glueframework.util.JsonSerializable;

@Entity(name = "OrderJpo")
public class OrderJpo implements JsonSerializable {
	@Id
	private String uuid;
	private String userId; // 사용자ID
	private String takeOutId; // 테이크아웃 ID
	private String orderQuantity; // 주문수량
	private String status;

	@SuppressWarnings("unused")
	private OrderJpo() {
	}

	public OrderJpo(String userId, String takeOutId) {
		this.uuid = Generators.timeBasedGenerator().generate().toString();
		this.userId = userId;
		this.takeOutId = takeOutId;
		this.status = "init";
		this.orderQuantity = "1";
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTakeOutId() {
		return takeOutId;
	}

	public void setTakeOutId(String takeOutId) {
		this.takeOutId = takeOutId;
	}

	public String getOrderQuantity() {
		return orderQuantity;
	}

	public void setOrderQuantity(String orderQuantity) {
		this.orderQuantity = orderQuantity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
