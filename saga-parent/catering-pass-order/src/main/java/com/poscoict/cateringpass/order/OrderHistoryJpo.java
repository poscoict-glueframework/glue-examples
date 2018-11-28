package com.poscoict.cateringpass.order;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.fasterxml.uuid.Generators;

@Entity(name = "OrderTransactionJpo")
public class OrderHistoryJpo {
	@Id
	private String uuid;
	private String orderId;// OrderJpo의 ID
	private Date createdTime;
	private String status;
	@Lob
	private String receivedMessage;

	@SuppressWarnings("unused")
	private OrderHistoryJpo() {
	}

	public OrderHistoryJpo(OrderJpo jpo) {
		this.uuid = Generators.timeBasedGenerator().generate().toString();
		this.orderId = jpo.getUuid();
		this.createdTime = new Date(System.currentTimeMillis());
		this.status = jpo.getStatus();
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

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReceivedMessage() {
		return receivedMessage;
	}

	public void setReceivedMessage(String receivedMessage) {
		this.receivedMessage = receivedMessage;
	}

}
