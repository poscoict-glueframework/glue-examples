package com.poscoict.cateringpass.payment;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.uuid.Generators;
import com.poscoict.glueframework.util.JsonSerializable;

@Entity(name = "PaymentJpo")
public class PaymentJpo implements JsonSerializable {
	@Id
	private String uuid;
	private String orderId; // OrderID
	private String status; // 접수, 결제완료, 환불완료

	@SuppressWarnings("unused")
	private PaymentJpo() {
	}

	public PaymentJpo(String orderId) {
		this.uuid = Generators.timeBasedGenerator().generate().toString();
		this.orderId = orderId;
		this.status = "지불";
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
