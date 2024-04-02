FROM openjdk:11
MAINTAINER PangTeen
LABEL authors="PangTeen"
WORKDIR /
COPY target/problem-service.jar app.jar
EXPOSE 8001
ENTRYPOINT ["java", "-jar"]
CMD ["app.jar"]