package com.poscoict.sample.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.poscoict.glueframework.biz.activity.GlueActivityConstants;
import com.poscoict.glueframework.biz.control.GlueBizController;
import com.poscoict.glueframework.context.GlueContext;
import com.poscoict.glueframework.context.GlueDefaultContext;
import com.poscoict.sample.activity.Custom_CreateData;
import com.poscoict.sample.jpa.EmpJpo;
import com.poscoict.sample.jpa.EmpRepository;

@RestController
@RequestMapping( value = "/edu" )
public class SampleController
{
    @Autowired
    private GlueBizController bizController;

    @Autowired
    private Custom_CreateData activity;

    @GetMapping
    public String getVersion()
    {
        return "1.0.0";
    }

    @GetMapping( path = "{serviceName}" )
    public List<Map<String, String>> runGlueService( @PathVariable String serviceName )
    {
        // 1. GlueContext 생성
        GlueContext ctx = new GlueDefaultContext( serviceName );

        // 2. GlueService 실행
        bizController.doAction( ctx );

        // 3. [Optional] 결과반환
        this.print( ctx );
        return ctx.getResults();
    }

    @PostMapping( path = "{serviceName}", params = { "empno", "ename" } )
    public void runGlueActivity( @PathVariable String serviceName, @RequestParam String empno, @RequestParam String ename )
    {
        // 1. GlueContext 생성
        GlueContext ctx = new GlueDefaultContext( "no-service" );
        ctx.put( "empno", ename );
        ctx.put( "ename", ename );

        // 2. GlueActivity property 설정 후 GlueContext에 담기.
        Map<String, String> props = new HashMap<>();
        props.put( GlueActivityConstants.ACTIVITY_PROPERTY_PARAM_COUNT, "2" );
        props.put( "param1", "empno" );
        props.put( "param2", "ename" );
        ctx.setActivityProperties( activity.getClass(), props );

        // 3. GlueActivty 실행
        activity.runActivity( ctx );

        // 3. [Optional] 결과반환
        this.print( ctx );
    }

    private void print( GlueContext ctx )
    {
        for ( Entry<String, Object> entry : ctx.entrySet() )
        {
            System.out.println( "GlueContext 확인 : " + entry.getKey() + " : " + entry.getValue() );
        }

        List<Map<String, String>> results = ctx.getResults();
        for ( Map<String, String> map : results )
        {
            if ( map.containsKey( GlueActivityConstants.ACTIVITY_PROPERTY_RESULT_KEY ) )
            {
                String resultKey = map.get( GlueActivityConstants.ACTIVITY_PROPERTY_RESULT_KEY );
                Object resultValue = ctx.get( resultKey );
                System.out.println( "Activity [" + map.get( "name" ) + "] " + resultKey + " : " + resultValue );
            } else
            {
                System.out.println( "Activity [" + map.get( "name" ) + "] " );
            }
        }
    }

    @Autowired
    private EmpRepository repository;

    @PostConstruct
    public void init()
    {
        repository.deleteAll();
        repository.save( new EmpJpo( 1, "poscoict" ) );
        repository.save( new EmpJpo( 2, "spring" ) );
        repository.save( new EmpJpo( 3, "github" ) );
    }
}
