package sample.activity;

import org.springframework.stereotype.Component;

import com.poscoict.glueframework.biz.activity.GlueActivity;
import com.poscoict.glueframework.biz.activity.GlueActivityConstants;
import com.poscoict.glueframework.context.GlueContext;

@Component
public class HelloActivity extends GlueActivity<GlueContext> {
	@Override
	public String runActivity(GlueContext ctx) {
		System.out.println("ServiceName : " + ctx.getServiceName());
		System.out.println("This is '" + ctx.getActivityName() + "' activity");

		Object input = ctx.get("input");
		System.out.println("data : " + input);

		String belongTo = ctx.getProperty("belongTo");
		System.out.println("data : " + belongTo);

		Object output = "[" + belongTo + "] Hello " + input + "!!!";
		ctx.put("result", output);

		return GlueActivityConstants.SUCCESS;
	}
}