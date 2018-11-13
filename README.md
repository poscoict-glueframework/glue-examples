# glue-examples
Glue Frameowrk 5.1 예제입니다. 

## 사전 준비

local repository에 3rd-party(glue) 라이브러리를 등록하세요. 

1. GlueSDK-5.1.0-RELEASE.zip 을 download 한다.  
   https://www.solutionpot.co.kr 을 통해 배포예정입니다.  

```bash
$ unzip GlueSDK-5.1.0-RELEASE.zip       # 압축풀기
Archive:  GlueSDK-5.1.0-RELEASE.zip
   creating: GlueSDK/config/
   creating: GlueSDK/lib/
   creating: GlueSDK/lib/gluelib/
   creating: GlueSDK/lib/gluestd/
   creating: GlueSDK/repo/              # mvn install:install-file 실행 참고 위치
   creating: GlueSDK/templateFolder/
   creating: GlueSDK/templateFolder/maven/
   # 중략
```

2. mvn install:install-file 수행한다.

```bash
$ cd GlueSDK/repo                       # repo 디렉토리로 이동
$ ls -l                                 # 파일 목록 확인
total 840
-rw-r--r-- 1 poscoict 1049089  29231 Oct 31 17:44 glue-core-5.1.0-RELEASE.jar
-rw-r--r-- 1 poscoict 1049089   5457 Oct 31 17:44 glue-core-5.1.0-RELEASE.pom
-rw-r--r-- 1 poscoict 1049089   9873 Oct 31 17:44 glue-framework-5.1.0-RELEASE.pom
-rw-r--r-- 1 poscoict 1049089   4793 Oct 31 17:44 glue-push-5.1.0-RELEASE.jar
-rw-r--r-- 1 poscoict 1049089   5398 Oct 31 17:44 glue-push-5.1.0-RELEASE.pom
-rw-r--r-- 1 poscoict 1049089  18729 Oct 31 17:44 glue-schema-5.1.0-RELEASE.jar
-rw-r--r-- 1 poscoict 1049089   4947 Oct 31 17:44 glue-schema-5.1.0-RELEASE.pom
-rw-r--r-- 1 poscoict 1049089   7504 Oct 31 17:44 glue-ucube-5.1.0-RELEASE.jar
-rw-r--r-- 1 poscoict 1049089   5633 Oct 31 17:44 glue-ucube-5.1.0-RELEASE.pom
-rw-r--r-- 1 poscoict 1049089   7652 Oct 31 17:44 glue-util-5.1.0-RELEASE.jar
-rw-r--r-- 1 poscoict 1049089   3969 Oct 31 17:44 glue-util-5.1.0-RELEASE.pom
-rw-r--r-- 1 poscoict 1049089  17802 Oct 31 17:44 push-client-0.0.1.jar
-rw-r--r-- 1 poscoict 1049089 711182 Oct 31 17:44 seadapter-3.1.26.jar
$                                       # 순서대로 install-file 실행
$ mvn install:install-file -Dfile=glue-framework-5.1.0-RELEASE.pom -DpomFile=glue-framework-5.1.0-RELEASE.pom
$ mvn install:install-file -Dfile=glue-schema-5.1.0-RELEASE.jar    -DpomFile=glue-schema-5.1.0-RELEASE.pom
$ mvn install:install-file -Dfile=glue-util-5.1.0-RELEASE.jar      -DpomFile=glue-util-5.1.0-RELEASE.pom
$ mvn install:install-file -Dfile=glue-core-5.1.0-RELEASE.jar      -DpomFile=glue-core-5.1.0-RELEASE.pom
```

## 실행

```bash
git clone https://github.com/poscoict-glueframework/glue-examples.git
cd glue-examples
``` 

```bash
mvn clean package -Pall              # 전체빌드

mvn clean package -Pquick-start      # quick-start 만 빌드     ( cd quick-start/;mvn package )
mvn clean package -Pcatering-pass    # catering-pass 만 빌드 ( cd catering-pass/;mvn package ) 
mvn clean package -Pjob-fcm          # job-fcm 만 빌드              ( cd job-fcm/;mvn package ) 
```

```bash
java -jar quick-start/target/demo.jar                    # quick-start 실행

java -jar catering-pass/target/catering-pass.jar         # catering-pass 실행

java -jar job-fcm/target/job-fcm.jar                     # job-fcm 실행
```



### 예시

```bash
$ git clone https://github.com/poscoict-glueframework/glue-examples.git  # clone 수행
Cloning into 'glue-examples'...
remote: Enumerating objects: 28, done.
remote: Counting objects: 100% (28/28), done.
remote: Compressing objects: 100% (16/16), done.
remote: Total 28 (delta 0), reused 25 (delta 0), pack-reused 0
Unpacking objects: 100% (28/28), done.
$ cd glue-examples/quick-start/                                          # quick-start 디렉토리로 이동
$ mvn package                                                            # package 수행 및 결과 확인. 
[INFO] Scanning for projects...
...                                                                      # 중략 
[INFO] --- maven-jar-plugin:3.0.2:jar (default-jar) @ demo ---
[INFO] Building jar: C:\works\eclipse-workspace\glue-examples\quick-start\target\demo-0.0.1-SNAPSHOT.jar
[INFO]
[INFO] --- spring-boot-maven-plugin:2.0.4.RELEASE:repackage (default) @ demo ---
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 19.180 s
[INFO] Finished at: 2018-10-31T17:56:05+09:00
[INFO] ------------------------------------------------------------------------
$ java -jar ./target/demo-0.0.1-SNAPSHOT.jar                             # 테스트 수행

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.0.4.RELEASE)

2018-10-31 18:01:40.856  INFO 72068 --- [           main] com.example.demo.DemoApplication         : Starting DemoApplication v0.0.1-SNAPSHOT on PICPOSCOICT with PID 72068 (C:\works\eclipse-workspace\glue-examples\quick-start\target\demo-0.0.1-SNAPSHOT.jar started by poscoict in C:\works\eclipse-workspace\glue-examples\quick-start)
2018-10-31 18:01:40.856  INFO 72068 --- [           main] com.example.demo.DemoApplication         : No active profile set, falling back to default profiles: default
2018-10-31 18:01:41.014  INFO 72068 --- [           main] s.c.a.AnnotationConfigApplicationContext : Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@19e1023e: startup date [Wed Oct 31 18:01:41 KST 2018]; root of context hierarchy
2018-10-31 18:01:43.208  INFO 72068 --- [           main] o.s.c.ehcache.EhCacheManagerFactoryBean  : Initializing EhCache CacheManager
2018-10-31 18:01:45.416  INFO 72068 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Registering beans for JMX exposure on startup
2018-10-31 18:01:45.416  INFO 72068 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Bean with name 'dataSource' has been autodetected for JMX exposure
2018-10-31 18:01:45.432  INFO 72068 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Located MBean 'dataSource': registering with JMX server as MBean [com.zaxxer.hikari:name=dataSource,type=HikariDataSource]
2018-10-31 18:01:45.463  INFO 72068 --- [           main] com.example.demo.DemoApplication         : Started DemoApplication in 5.612 seconds (JVM running for 6.809)
2018-10-31 18:01:45.479  INFO 72068 --- [           main] c.p.g.biz.control.GlueBizController      : ServiceName:[hello-service] StartTime[Wed Oct 31 18:01:45 KST 2018]
ServiceName : hello-service
This is 'HelloActivity' activity
data : Glue
data : POSCOICT
2018-10-31 18:01:45.855  INFO 72068 --- [           main] c.p.g.biz.control.GlueBizController      : ServiceName:[hello-service] EndTime[Wed Oct 31 18:01:45 KST 2018] RunTime:[376]
[POSCOICT] Hello Glue!!!
2018-10-31 18:01:45.855  INFO 72068 --- [       Thread-4] s.c.a.AnnotationConfigApplicationContext : Closing org.springframework.context.annotation.AnnotationConfigApplicationContext@19e1023e: startup date [Wed Oct 31 18:01:41 KST 2018]; root of context hierarchy
2018-10-31 18:01:45.855  INFO 72068 --- [       Thread-4] o.s.j.e.a.AnnotationMBeanExporter        : Unregistering JMX-exposed beans on shutdown
2018-10-31 18:01:45.855  INFO 72068 --- [       Thread-4] o.s.j.e.a.AnnotationMBeanExporter        : Unregistering JMX-exposed beans
```


