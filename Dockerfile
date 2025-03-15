FROM amazoncorretto:21
WORKDIR /app
COPY ./build/libs/app-server-0.0.1-SNAPSHOT.jar /app/app-application.jar

ARG SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE

CMD ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "app-application.jar"]
