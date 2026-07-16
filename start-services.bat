@echo off
set BASE=E:\?????\????\????\Project\bookhaven

echo Starting BookHaven Services...

start "bookhaven-user" /MIN java -jar "%BASE%\user-service\target\user-service-1.0.0.jar"
timeout /t 5 /nobreak >nul

start "bookhaven-product" /MIN java -jar "%BASE%\product-service\target\product-service-1.0.0.jar"
timeout /t 5 /nobreak >nul

start "bookhaven-cart" /MIN java -jar "%BASE%\cart-service\target\cart-service-1.0.0.jar"
timeout /t 5 /nobreak >nul

start "bookhaven-order" /MIN java -jar "%BASE%\order-service\target\order-service-1.0.0.jar"
timeout /t 5 /nobreak >nul

start "bookhaven-payment" /MIN java -jar "%BASE%\payment-service\target\payment-service-1.0.0.jar"
timeout /t 5 /nobreak >nul

start "bookhaven-gateway" /MIN java -jar "%BASE%\bookhaven-gateway\target\bookhaven-gateway-1.0.0.jar"

echo All services launched. Waiting for startup...
timeout /t 40 /nobreak

echo.
echo === Checking Ports ===
netstat -ano | findstr ":8081 " >nul && echo 8081 user-service      : RUNNING || echo 8081 user-service      : NOT RUNNING
netstat -ano | findstr ":8082 " >nul && echo 8082 product-service   : RUNNING || echo 8082 product-service   : NOT RUNNING
netstat -ano | findstr ":8083 " >nul && echo 8083 cart-service      : RUNNING || echo 8083 cart-service      : NOT RUNNING
netstat -ano | findstr ":8084 " >nul && echo 8084 order-service     : RUNNING || echo 8084 order-service     : NOT RUNNING
netstat -ano | findstr ":8085 " >nul && echo 8085 payment-service   : RUNNING || echo 8085 payment-service   : NOT RUNNING
netstat -ano | findstr ":8088 " >nul && echo 8088 gateway           : RUNNING || echo 8088 gateway           : NOT RUNNING

echo.
echo BookHaven startup complete!
pause
