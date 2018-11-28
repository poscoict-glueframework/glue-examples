# Saga 패턴 적용

2개의 profile 사용가능합니다.
- saga
- saga-cloud

## how to run

```bach
cd apache-activemq-5.15.7/bin
activemq start
```

```bash
$ git clone https://github.com/poscoict-glueframework/glue-examples.git
$ cd glue-examples/saga-parent
$ mvn clean package
$ #mvn clean package -Psaga
$ #mvn clean package -Psaga-cloud
$ java -jar catering-pass-order/target/catering-pass-order.jar
$ java -jar catering-pass-payment/target/catering-pass-payment.jar
$ java -jar catering-pass-stock/target/catering-pass-stock.jar
$ java -jar catering-pass-delivery/target/catering-pass-delivery.jar
```
