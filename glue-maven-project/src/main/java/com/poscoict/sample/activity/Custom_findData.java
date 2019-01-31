package com.poscoict.sample.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.poscoict.glueframework.biz.activity.GlueActivity;
import com.poscoict.glueframework.biz.activity.GlueActivityConstants;
import com.poscoict.glueframework.context.GlueContext;
import com.poscoict.sample.jpa.EmpJpo;
import com.poscoict.sample.jpa.EmpRepository;

@Component
public class Custom_findData extends GlueActivity<GlueContext>
{
    @Autowired
    private EmpRepository repository;

    @Override
    public String runActivity( GlueContext ctx )
    {
        this.logger.info( "##### {}, activity name  : {}", ctx.getServiceName(), ctx.getActivityName() );

        Iterable<EmpJpo> jpos = repository.findAll();
        String key = ctx.getProperty( GlueActivityConstants.ACTIVITY_PROPERTY_RESULT_KEY );
        ctx.put( key, jpos );

        return GlueActivityConstants.ACTIVTY_TRANSITION_SUCCESS;
    }
}
