package com.poscoict.sample.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.poscoict.glueframework.biz.activity.GlueActivity;
import com.poscoict.glueframework.biz.activity.GlueActivityConstants;
import com.poscoict.glueframework.context.GlueContext;
import com.poscoict.sample.jpa.EmpJpo;
import com.poscoict.sample.jpa.EmpRepository;

@Component
public class Custom_CreateData extends GlueActivity<GlueContext>
{
    @Autowired
    private EmpRepository repository;

    @Override
    public String runActivity( GlueContext ctx )
    {
        this.logger.info( "##### {}, activity name  : {}", ctx.getServiceName(), ctx.getActivityName() );

        EmpJpo jpo = new EmpJpo();

        /*
         * case 1.
         */
        // jpo.setEmpno( Integer.parseInt( (String) ctx.get( "empno" ) ) );
        // jpo.setEname( (String) ctx.get( "ename" ) );

        /*
         * case 2.
         */
        // jpo.setEmpno( Integer.parseInt( (String) ctx.get( ctx.getProperty( "param1" ) ) ) );
        // jpo.setEname( (String) ctx.get( ctx.getProperty( "param1" ) ) );

        /*
         * case 3.
         */
        int paramCnt = 0;
        String value = ctx.getProperty( GlueActivityConstants.ACTIVITY_PROPERTY_PARAM_COUNT );
        if ( value == null || value.trim().length() == 0 )
        {
            value = "0";
        }
        paramCnt = Integer.parseInt( value );
        // int paramCnt = super.getParamCount( ctx.getProperty( GlueActivityConstants.ACTIVITY_PROPERTY_PARAM_COUNT, true ) );

        for ( int i = 1; i <= paramCnt; i++ )
        {
            String property = ctx.getProperty( "param" + i );
            switch ( property )
            {
                case "empno":
                    jpo.setEmpno( Integer.parseInt( (String) ctx.get( property ) ) );
                    break;
                case "ename":
                    jpo.setEname( (String) ctx.get( property ) );
                default:
                    break;
            }
        }

        this.repository.save( jpo );
        return GlueActivityConstants.ACTIVTY_TRANSITION_SUCCESS;
    }
}
