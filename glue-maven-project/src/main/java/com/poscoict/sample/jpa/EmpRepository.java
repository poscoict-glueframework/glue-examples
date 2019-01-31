package com.poscoict.sample.jpa;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface EmpRepository extends PagingAndSortingRepository<EmpJpo, String> {
    Optional<EmpJpo> findByEname( String name );
}
