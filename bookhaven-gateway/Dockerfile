FROM xpj2mydrmryrbb24kv.xuanyuan.run/library/eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/bookhaven-gateway-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
