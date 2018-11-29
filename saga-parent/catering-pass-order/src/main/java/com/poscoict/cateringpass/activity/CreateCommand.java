package com.poscoict.cateringpass.activity;

import org.springframework.stereotype.Component;

import com.poscoict.glueframework.biz.activity.GlueActivity;
import com.poscoict.glueframework.context.GlueContext;

@Component
public class CreateCommand extends GlueActivity<GlueContext> {
	public static final String COMMAND = "command";

	public String runActivity(GlueContext ctx) {
		ctx.put(COMMAND, ctx.getProperty(COMMAND, true));
		return SUCCESS;
	}
}