package com.poscoict.cateringpass.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poscoict.glueframework.biz.activity.GlueRestClientActivity;
import com.poscoict.glueframework.context.GlueContext;
import com.poscoict.glueframework.context.GlueDefaultContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@Api(value = "CateringPass Saga", description = "사용자 앱 API - Saga(테이크아웃 주문)", tags = { "CateringPass Saga" })
@RestController
@RequestMapping(value = "/cateringpass-saga")
public class CateringPassSagaController {
	@Autowired
	private GlueRestClientActivity activity;

	// Order Service 주소
	@Value("${test.uri.order-service:http://localhost:8081/orderF}")
	private String orderServiceUri;

	@ApiOperation(value = "테이크 아웃 주문(주문 프로세스)")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "takeOutId", value = "테이크 아웃ID", required = true, dataType = "string", paramType = "query", defaultValue = ""),
			@ApiImplicitParam(name = "userId", value = "주문자ID", required = true, dataType = "string", paramType = "query", defaultValue = "") })
	@PostMapping(params = { "takeOutId", "userId" })
	public void order_takeout(@RequestParam String takeOutId, @RequestParam String userId) {
		System.out.println("POST /cateringpass-saga");

		// GlueContext 생성
		GlueContext ctx = new GlueDefaultContext("no-service");
		ctx.put("order-service-uri", orderServiceUri);
		ctx.put("userId", userId);
		ctx.put("takeOutId", takeOutId);

		// activity 의 property 정보
		Map<String, String> props = new HashMap<>();
		props.put("uri", "order-service-uri");
		props.put("method", "POST");
		props.put("param-count", "2");
		props.put("param1", "userId");
		props.put("param2", "takeOutId");
		props.put("result-key", "result");

		// activity 실행
		ctx.setActivityProperties(this.activity.getClass(), props);
		activity.runActivity(ctx);
	}
}
