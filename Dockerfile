FROM openjdk:11
EXPOSE 9000
RUN mkdir -p app
COPY build/libs/booking-*.jar booking.jar
WORKDIR app
ARG JAR_FILE=booking.jar
ADD . booking.jar
ENTRYPOINT ["java","-jar","/booking.jar"]