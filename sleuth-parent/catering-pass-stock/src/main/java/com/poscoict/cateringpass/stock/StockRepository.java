package com.poscoict.cateringpass.stock;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<StockJpo, String> {

}
