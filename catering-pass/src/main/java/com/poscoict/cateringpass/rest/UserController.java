package com.poscoict.cateringpass.rest;

import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poscoict.cateringpass.jpa.OrderRepository;
import com.poscoict.cateringpass.jpa.TakeOutRepository;
import com.poscoict.cateringpass.jpa.UserJpo;
import com.poscoict.cateringpass.jpa.UserRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "User", description = "식수 인증 API입니다.", tags = { "User" })
@RestController
@RequestMapping(value = "/auth")
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	TakeOutRepository takeOutRepository;

	@ApiOperation(value = "로그인 by 모바일앱")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "company", value = "회사 ( ex. poscoict )", required = true, dataType = "string", paramType = "query", defaultValue = ""),
			@ApiImplicitParam(name = "empno", value = "사번 ( ex. 11111 )", required = true, dataType = "string", paramType = "query", defaultValue = "") })
	@GetMapping(path = "login", params = { "company", "empno" })
	public UserJpo login(@RequestParam String company, @RequestParam String empno) {
		System.out.println("GET /auth/login");
		Optional<UserJpo> value = userRepository.findByEmpnoAndCompany(empno, company);
		if (value.isPresent()) {
			return value.get();
		}
		return null;
	}

	@ApiOperation(value = "고객 목록 보기 by 시스템 관리자")
	@GetMapping
	public List<UserJpo> users() {
		System.out.println("GET /auth");
		List<UserJpo> value = userRepository.findAll();
		return value;
	}

	@PostConstruct
	public void init() {
		if (userRepository.findAll().isEmpty()) {
			UserJpo user = new UserJpo("11111", "poscoict", "박재환");
			userRepository.save(user);
			user = new UserJpo("22222", "poscoict", "우인재");
			userRepository.save(user);
		}
	}
}
