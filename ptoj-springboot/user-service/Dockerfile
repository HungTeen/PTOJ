FROM openjdk:11
MAINTAINER PangTeen
LABEL authors="PangTeen"
WORKDIR /
COPY target/user-service.jar app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar"]
CMD ["app.jar"]