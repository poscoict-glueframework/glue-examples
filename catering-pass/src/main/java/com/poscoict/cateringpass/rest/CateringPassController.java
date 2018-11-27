package com.poscoict.cateringpass.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poscoict.cateringpass.jpa.OrderJpo;
import com.poscoict.cateringpass.jpa.TakeOutJpo;
import com.poscoict.cateringpass.jpa.TakeOutRepository;
import com.poscoict.glueframework.biz.control.GlueBizController;
import com.poscoict.glueframework.context.GlueContext;
import com.poscoict.glueframework.context.GlueDefaultContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "CateringPass", description = "사용자 앱 API(테이크아웃 예약 및 확인)", tags = { "CateringPass" })
@RestController
@RequestMapping(value = "/cateringpass")
public class CateringPassController {
	@Autowired
	GlueBizController bizController;

	@Autowired
	TakeOutRepository takeOutRepository;

	@SuppressWarnings("unchecked")
	@ApiOperation(value = "테이크 아웃 예약(메뉴 조회)")
	@GetMapping
	public List<TakeOutJpo> getTakeoutList() {
		System.out.println("GET /cateringpass/");
		GlueContext ctx = new GlueDefaultContext("order-service");
		bizController.doAction(ctx);
		return (List<TakeOutJpo>) ctx.get("takeoutList");
	}

	@ApiOperation(value = "테이크 아웃 예약(예약내역 조회)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "주문자ID", required = true, dataType = "string", paramType = "path", defaultValue = "") })
	@GetMapping(path = "{userId}")
	public Object order_check(@PathVariable String userId) {
		System.out.println("GET /cateringpass/" + userId);
		GlueContext ctx = new GlueDefaultContext("order-service");
		ctx.put("userId", userId);
		ctx.put("find", "find");

		bizController.doAction(ctx);

		List<Map<String, String>> list = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<OrderJpo> jpos = (List<OrderJpo>) ctx.get("orderList");
		for (OrderJpo orderJpo : jpos) {
			TakeOutJpo takeOutjpo = takeOutRepository.findById(orderJpo.getTakeOutId()).get();
			Map<String, String> map = new HashMap<>();
			map.put("dayStr", takeOutjpo.getDayStr());
			map.put("menuName", takeOutjpo.getMenu());
			map.put("price", "" + takeOutjpo.getPrice());
			map.put("menuStatus", takeOutjpo.isClose() ? "취소불가" : "");
			map.put("orderStatus", orderJpo.getOrderStatus());
			map.put("orderId", orderJpo.getUuid());
			list.add(map);
		}
		return list;
	}

	@ApiOperation(value = "테이크 아웃 예약(예약 정보 생성)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "takeOutId", value = "테이크 아웃ID", required = true, dataType = "string", paramType = "query", defaultValue = ""),
			@ApiImplicitParam(name = "userId", value = "주문자ID", required = true, dataType = "string", paramType = "query", defaultValue = "") })
	@PostMapping(params = { "takeOutId", "userId" })
	public List<Map<String, String>> order_takeout(@RequestParam String takeOutId, @RequestParam String userId) {
		System.out.println("POST /cateringpass/");
		GlueContext ctx = new GlueDefaultContext("order-service");
		ctx.put("takeOutId", takeOutId);
		ctx.put("userId", userId);
		ctx.put("add", "add");

		bizController.doAction(ctx);
		return ctx.getResults();
	}

	@ApiOperation(value = "테이크 아웃 예약(예약 취소)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "orderId", value = "예약 ID", required = true, dataType = "string", paramType = "path", defaultValue = "") })
	@PutMapping(path = "{orderId}")
	public void cancel_takeout(@PathVariable String orderId) {
		System.out.println("PUT /cateringpass/" + orderId);
		GlueContext ctx = new GlueDefaultContext("order-service");
		ctx.put("orderId", orderId);
		ctx.put("cancel", "cancel");
		bizController.doAction(ctx);
	}

	@PostConstruct
	public void init() {
		if (takeOutRepository.findAll().isEmpty()) {
			Date date = new Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000);
			TakeOutJpo takeOut = new TakeOutJpo(date, "햄에그 샌드위치", "판교식당", 2500);
			takeOut.setImage("http://recipe.ezmember.co.kr/cache/recipe/2017/04/25/8fc9f731dfe3be74a1ff26a5440b33f91.jpg");
			takeOutRepository.save(takeOut);
			takeOut = new TakeOutJpo(date, "샐러드", "판교식당", 2500);
			takeOut.setImage("https://m.dunkindonuts.co.kr/upload/product/1517795541.png");
			takeOutRepository.save(takeOut);

			date = new Date(System.currentTimeMillis() + 4 * 24 * 60 * 60 * 1000);
			takeOut = new TakeOutJpo(date, "크로와상 샌드위치", "판교식당", 2500);
			takeOut.setImage("http://imagescdn.gettyimagesbank.com/500/201708/jv10965573.jpg");
			takeOutRepository.save(takeOut);
			takeOut = new TakeOutJpo(date, "샐러드", "판교식당", 2500);
			takeOut.setImage("https://www.paris.co.kr/data/product/Caesar.jpg");
			takeOutRepository.save(takeOut);

			date = new Date(System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000);
			takeOut = new TakeOutJpo(date, "클럽 샌드위치", "판교식당", 2500);
			takeOut.setImage("https://t1.daumcdn.net/cfile/tistory/2573CD4454CB107016");
			takeOutRepository.save(takeOut);
			takeOut = new TakeOutJpo(date, "샐러드", "판교식당", 2500);
			takeOut.setImage("https://www.dunkindonuts.co.kr/upload/product/1517795612.png");
			takeOutRepository.save(takeOut);
		}
	}
}
