package com.poscoict.cateringpass.rest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Constants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poscoict.cateringpass.jpa.MonthlyJpo;
import com.poscoict.cateringpass.jpa.MonthlyRepository;
import com.poscoict.cateringpass.jpa.OrderJpo;
import com.poscoict.cateringpass.jpa.OrderRepository;
import com.poscoict.cateringpass.jpa.TakeOutJpo;
import com.poscoict.cateringpass.jpa.TakeOutRepository;
import com.poscoict.cateringpass.jpa.UserJpo;
import com.poscoict.cateringpass.jpa.UserRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "Manager", description = "관리 API (주문 마감)입니다.", tags = { "Manager" })
@RestController
@RequestMapping(value = "/mgr")
public class ManagerController {
	static final Constants constants = new Constants(Calendar.class);

	@Autowired
	UserRepository userRepository;

	@Autowired
	OrderRepository orderRepository;

	@Autowired
	TakeOutRepository takeOutRepository;

	@Autowired
	MonthlyRepository monthlyRepository;

	@ApiOperation(value = "예약 물량 확인하기 by 영양사")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "year", value = "주문일(년)", required = true, dataType = "string", paramType = "query", defaultValue = "2018"),
			@ApiImplicitParam(name = "month", value = "주문일(월)", required = true, dataType = "string", paramType = "query", defaultValue = "NOVEMBER"),
			@ApiImplicitParam(name = "day", value = "주문일(일)", required = true, dataType = "string", paramType = "query", defaultValue = "7") })
	@GetMapping(params = { "year", "month", "day" })
	public List<Map<String, String>> getCount(@RequestParam String year, @RequestParam String month,
			@RequestParam String day) {
		System.out.println("GET /mgr");
		// 날짜변환(String -> Date)
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(year), constants.asNumber(month).intValue(), Integer.parseInt(day));
		Date date = calendar.getTime();

		List<Map<String, String>> returnVal = new ArrayList<>();

		// 특정일의 메뉴 확인하기 -> 메뉴ID 확보
		List<TakeOutJpo> value = takeOutRepository.findByDay(date);
		for (TakeOutJpo takeOut : value) {
			Map<String, String> map = new HashMap<>();
			map.put("menu", takeOut.getMenu()); // 메뉴가 뭔지?

			// 메뉴ID별로 주문 수량 확인하기 ( 취소/대기/확정 3가지 상태 가능 )
			String takeOutId = takeOut.getUuId(); // 메뉴ID
			Long cnt = orderRepository.countByTakeOutIdAndOrderStatus(takeOutId, "대기");
			map.put("cnt", "" + cnt);
			if (cnt == 0) {
				cnt = orderRepository.countByTakeOutIdAndOrderStatus(takeOutId, "확정");
				map.put("cnt", "" + cnt);
			}
			returnVal.add(map);
		}
		return returnVal;
	}

	@ApiOperation(value = "월별 합산 금액 확인하기 by 사용자 ")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "사용자ID", required = true, dataType = "string", paramType = "path", defaultValue = "") })
	@GetMapping(path = "{userId}")
	public List<MonthlyJpo> getMonthy(@PathVariable String userId) {
		System.out.println("GET /mgr/" + userId);
		return monthlyRepository.findByUserId(userId);
	}

	@ApiOperation(value = "예약 접수 마감 by scheduler, 매일 밤 12시")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "year", value = "주문일(년)", required = true, dataType = "string", paramType = "query", defaultValue = "2018"),
			@ApiImplicitParam(name = "month", value = "주문일(월)", required = true, dataType = "string", paramType = "query", defaultValue = "NOVEMBER"),
			@ApiImplicitParam(name = "day", value = "주문일(일)", required = true, dataType = "string", paramType = "query", defaultValue = "7") })
	@PutMapping(params = { "year", "month", "day" })
	public int confirm(@RequestParam String year, @RequestParam String month, @RequestParam String day) {
		System.out.println("PUT /mgr/");
		// 날짜변환(String -> Date)
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(year), constants.asNumber(month).intValue(), Integer.parseInt(day));
		Date date = calendar.getTime();

		// 특정일의 메뉴 확인하기 -> 메뉴ID 확보
		List<TakeOutJpo> value = takeOutRepository.findByDay(date);
		for (TakeOutJpo jpo : value) {
			String takeOutId = jpo.getUuId();
			jpo.setClose(true);
			takeOutRepository.save(jpo); // 주문 마감 처리

			// 주문 정보 ( 대기 -> 확정 ) 변경하기
			List<OrderJpo> jpos = orderRepository.findByTakeOutIdAndOrderStatus(takeOutId, "대기");
			for (OrderJpo order : jpos) {
				order.setOrderStatus("확정");
				orderRepository.save(order);
			}
		}
		return value.size();
	}

	@ApiOperation(value = "전전월 16일~전월 15일까지의 주문금액 합산하기 by scheduler, 매월 1일 00시   ")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "year", value = "정산기준일(년)", required = true, dataType = "string", paramType = "path", defaultValue = "2018"),
			@ApiImplicitParam(name = "month", value = "정산기준일(월)", required = true, dataType = "string", paramType = "path", defaultValue = "NOVEMBER") })
	@PostMapping(path = "{year}/{month}")
	public void calculate(@PathVariable String year, @PathVariable String month) {
		System.out.println("POST /mgr/" + year + "/" + month);

		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(year), constants.asNumber(month).intValue(), 1);

		Date mday = calendar.getTime();

		if (constants.asNumber("JANUARY").intValue() == constants.asNumber(month).intValue()) {
			calendar.set(Integer.parseInt(year) - 1, constants.asNumber("NOVEMBER").intValue(), 1);
		} else if (constants.asNumber("FEBRUARY").intValue() == constants.asNumber(month).intValue()) {
			calendar.set(Integer.parseInt(year) - 1, constants.asNumber("DECEMBER").intValue(), 1);
		} else {
			calendar.set(Integer.parseInt(year), constants.asNumber(month).intValue() - 2, 1);
		}
		Date fday = calendar.getTime();

		if (constants.asNumber("JANUARY").intValue() == constants.asNumber(month).intValue()) {
			calendar.set(Integer.parseInt(year) - 1, constants.asNumber("DECEMBER").intValue(), 1);
		} else {
			calendar.set(Integer.parseInt(year), constants.asNumber(month).intValue() - 1, 1);
		}
		Date tday = calendar.getTime();

		long amount = 0;
		List<UserJpo> users = userRepository.findAll();
		for (UserJpo userJpo : users) {
			List<OrderJpo> orders = orderRepository.findByUserIdAndOrderStatusAndOrderDayBetween(userJpo.getUuid(),
					"확정", fday, tday);
			for (OrderJpo orderJpo : orders) {
				amount += takeOutRepository.findById(orderJpo.getTakeOutId()).get().getPrice();
			}
			MonthlyJpo jpo = new MonthlyJpo(userJpo.getCompany(), new SimpleDateFormat("yyyy/MM").format(mday), amount);
			jpo.setFromDay(new SimpleDateFormat("yyyy/MM/dd").format(fday));
			jpo.setToDay(new SimpleDateFormat("yyyy/MM/dd").format(tday));
			monthlyRepository.save(jpo);
		}
	}
}
