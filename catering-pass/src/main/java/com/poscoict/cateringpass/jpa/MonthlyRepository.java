package com.poscoict.cateringpass.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyRepository extends JpaRepository<MonthlyJpo, String> {
	List<MonthlyJpo> findByUserId(String userId);
}
