package com.poscoict.cateringpass.jpa;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.uuid.Generators;
import com.poscoict.glueframework.util.JsonSerializable;

@Entity(name = "UserJpo")
public class UserJpo implements Serializable, JsonSerializable {
	private static final long serialVersionUID = -5850455668863023992L;
	@Id
	private String uuid;
	private String empno;
	private String name;
	private String company;

	public UserJpo() {
	}

	public UserJpo(String empno, String company, String name) {
		this.uuid = Generators.timeBasedGenerator().generate().toString();
		this.company = company;
		this.empno = empno;
		this.name = name;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getEmpno() {
		return empno;
	}

	public void setEmpno(String empno) {
		this.empno = empno;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
}
