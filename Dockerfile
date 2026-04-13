FROM amazoncorretto:21
WORKDIR /app
COPY ./build/libs/app-server-0.0.1-SNAPSHOT.jar /app/app-application.jar

CMD ["java", "-jar", "app-application.jar"]