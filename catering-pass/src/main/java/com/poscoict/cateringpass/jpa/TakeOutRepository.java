package com.poscoict.cateringpass.jpa;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TakeOutRepository extends JpaRepository<TakeOutJpo, String> {
	List<TakeOutJpo> findByDay(Date day);

	List<TakeOutJpo> findByDayBetween(Date from, Date to);
}
