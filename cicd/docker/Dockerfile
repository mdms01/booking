FROM openjdk:11
EXPOSE 9000
RUN mkdir -p app
COPY build/libs/booking-*.jar app/booking.jar
WORKDIR app
ARG JAR_FILE=app/booking.jar
ADD ${JAR_FILE} booking.jar
ENTRYPOINT ["java","-jar","/booking.jar"]