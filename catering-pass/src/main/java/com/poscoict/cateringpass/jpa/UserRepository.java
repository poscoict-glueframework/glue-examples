package com.poscoict.cateringpass.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserJpo, String> {
	Optional<UserJpo> findByEmpnoAndCompany(String empno, String company);
}
