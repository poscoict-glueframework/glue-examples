package com.poscoict.cateringpass.stock;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.uuid.Generators;

@Entity(name = "StockHistoryJpo")
public class StockHistoryJpo {
	@Id
	private String uuid;
	private String stockId;
	private Date createdTime;
	private String status;

	@SuppressWarnings("unused")
	private StockHistoryJpo() {
	}

	public StockHistoryJpo(StockJpo jpo) {
		this.uuid = Generators.timeBasedGenerator().generate().toString();
		this.stockId = jpo.getUuid();
		this.status = jpo.getStatus();
		this.createdTime = new Date(System.currentTimeMillis());
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
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

}
