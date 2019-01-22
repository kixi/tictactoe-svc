FROM openjdk:8-alpine

COPY target/uberjar/tictactoe-svc.jar /tictactoe-svc/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/tictactoe-svc/app.jar"]
