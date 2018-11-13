package com.poscoict.cateringpass.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.uuid.Generators;
import com.poscoict.glueframework.util.JsonSerializable;

@Entity(name = "OrderJpo")
public class OrderJpo implements Serializable, JsonSerializable {
	private static final long serialVersionUID = -2163124315126876872L;
	@Id
	private String uuid;
	private String userId;
	private String takeOutId;
	private String orderStatus;
	private Date orderDay;

	public OrderJpo() {
	}

	public OrderJpo(String userId, String takeOutId) {
		this.uuid = Generators.timeBasedGenerator().generate().toString();
		this.orderDay = new Date(System.currentTimeMillis());
		this.userId = userId;
		this.takeOutId = takeOutId;
		this.orderStatus = "대기";
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

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Date getOrderDay() {
		return orderDay;
	}

	public void setOrderDay(Date orderDay) {
		this.orderDay = orderDay;
	}
}
