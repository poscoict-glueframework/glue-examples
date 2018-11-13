package com.poscoict.cateringpass.jpa;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.uuid.Generators;
import com.poscoict.glueframework.util.JsonSerializable;

@Entity(name = "MonthlyJpo")
public class MonthlyJpo implements Serializable, JsonSerializable {
	private static final long serialVersionUID = 1526564652003767294L;
	@Id
	private String uuid;
	private String userId;
	private String month;
	private String fromDay;
	private String toDay;
	private Long amount;

	public MonthlyJpo() {
	}

	public MonthlyJpo(String userId, String month, Long amount) {
		this.uuid = Generators.timeBasedGenerator().generate().toString();
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

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getFromDay() {
		return fromDay;
	}

	public void setFromDay(String fromDay) {
		this.fromDay = fromDay;
	}

	public String getToDay() {
		return toDay;
	}

	public void setToDay(String toDay) {
		this.toDay = toDay;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}
}
