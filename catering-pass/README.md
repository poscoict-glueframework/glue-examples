# Catering Pass

2개의 profile 사용가능합니다.
- catering-pass
- catering-pass-cloud

## how to build

```bash
$ git clone https://github.com/poscoict-glueframework/glue-examples.git
$ cd glue-examples/catering-pass
$ mvn clean package
$ #mvn clean package -Pcatering-pass
$ #mvn clean package -Pcatering-pass-cloud
$ java -jar target/catering-pass.jar
```

### application.yml 수정시 

```bash
$ mvn clean package
$ java -jar target/catering-pass.jar --spring.config.location=file:/C:/application.properties
```

application.yml 예시

```yml
#glue.meta.source=classpath
glue.meta.source=http
glue.meta.http.url=http://localhost:9102/glue-service
```

### config-server 사용시 

```bash
$ mvn clean package -Pcatering-pass-cloud
$ java -jar target/catering-pass-api.jar --configServerUri=http://localhost:8888 --activatedProperties=local
```
