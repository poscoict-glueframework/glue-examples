package com.poscoict.cateringpass.jpa;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.uuid.Generators;
import com.poscoict.glueframework.util.JsonSerializable;

@Entity(name = "TakeOutJpo")
public class TakeOutJpo implements Serializable, JsonSerializable {
	private static final long serialVersionUID = -626965226291373590L;
	@Id
	private String uuid;
	private Date day;
	private String dayStr;
	private String dayOfWeek;
	private String menu;
	private String location;
	private String image;
	private int price;
	private boolean close;

	public TakeOutJpo() {
	}

	public TakeOutJpo(Date day, String menu, String location, int price) {
		this.uuid = Generators.timeBasedGenerator().generate().toString();
		this.day = day;
		this.dayStr = new SimpleDateFormat("MM/dd").format(day);
		this.dayOfWeek = new SimpleDateFormat("E").format(day);
		this.menu = menu;
		this.location = location;
		this.price = price;
		this.close = false;
	}

	public String getUuId() {
		return uuid;
	}

	public void setUuId(String uuid) {
		this.uuid = uuid;
	}

	public Date getDay() {
		return day;
	}

	public void setDay(Date day) {
		this.day = day;
		this.dayStr = new SimpleDateFormat("MM/dd").format(day);
		this.dayOfWeek = new SimpleDateFormat("E").format(day);
	}

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isClose() {
		return close;
	}

	public void setClose(boolean close) {
		this.close = close;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDayStr() {
		return dayStr;
	}

	public void setDayStr(String dayStr) {
		this.dayStr = dayStr;
	}

	public String getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
}
