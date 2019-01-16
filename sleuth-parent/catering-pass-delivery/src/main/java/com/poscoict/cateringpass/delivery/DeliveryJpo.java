package com.poscoict.cateringpass.delivery;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.uuid.Generators;
import com.poscoict.glueframework.util.JsonSerializable;

@Entity(name = "DeliveryJpo")
public class DeliveryJpo implements JsonSerializable {
	@Id
	private String uuid; // ID
	private String orderId; // 주문ID
	private String status; // 상태 ( 접수/배송중/배송완료 )

	public DeliveryJpo() {
	}

	public DeliveryJpo(String orderId) {
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
