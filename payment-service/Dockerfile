FROM xpj2mydrmryrbb24kv.xuanyuan.run/library/eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/payment-service-1.0.0.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]
