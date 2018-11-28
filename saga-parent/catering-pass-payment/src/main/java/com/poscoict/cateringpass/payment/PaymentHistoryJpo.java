package com.poscoict.cateringpass.payment;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.uuid.Generators;

@Entity(name = "PaymentHistoryJpo")
public class PaymentHistoryJpo {
	@Id
	private String uuid;
	private String paymentId;
	private Date createdTime;
	private String status;

	@SuppressWarnings("unused")
	private PaymentHistoryJpo() {
	}

	public PaymentHistoryJpo(PaymentJpo jpo) {
		this.uuid = Generators.timeBasedGenerator().generate().toString();
		this.paymentId = jpo.getUuid();
		this.status = jpo.getStatus();
		this.createdTime = new Date(System.currentTimeMillis());
	}

	public String getUuid() {
		return uuid;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public String getStatus() {
		return status;
	}
}
